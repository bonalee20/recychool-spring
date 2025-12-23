package com.app.recychool.service;

import com.app.recychool.domain.dto.reserve.ReserveCreateRequestDTO;
import com.app.recychool.domain.dto.reserve.ReserveCreateResponseDTO;
import com.app.recychool.domain.entity.Reserve;
import com.app.recychool.domain.entity.School;
import com.app.recychool.domain.entity.User;
import com.app.recychool.domain.enums.ReserveStatus;
import com.app.recychool.domain.enums.ReserveType;
import com.app.recychool.exception.ReserveException;
import com.app.recychool.repository.ReserveRepository;
import com.app.recychool.repository.SchoolRepository;
import com.app.recychool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReserveServiceImpl implements ReserveService {

    private final ReserveRepository reserveRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;

    private static final int PARKING_AREA_PER_CAR = 100;

    @Override
    public ReserveCreateResponseDTO createReserve(
            Long userId,
            Long schoolId,
            ReserveType reserveType,
            ReserveCreateRequestDTO requestDTO
    ) {
        if (userId == null) throw new ReserveException("로그인이 필요합니다.");
        if (schoolId == null) throw new ReserveException("학교 정보가 올바르지 않습니다.");
        if (reserveType == null) throw new ReserveException("예약 타입이 올바르지 않습니다.");
        if (requestDTO == null || requestDTO.getStartDate() == null) {
            throw new ReserveException("예약 날짜가 필요합니다.");
        }

        // 0. 사용자 예약 횟수 검증 (주차 1 / 장소대여 2)
        if (reserveType == ReserveType.PARKING) {
            validateParkingLimit(userId);
        } else if (reserveType == ReserveType.PLACE) {
            validatePlaceLimit(userId);
        } else {
            throw new ReserveException("지원하지 않는 예약 타입입니다.");
        }

        // 1. 사용자 / 학교 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ReserveException("존재하지 않는 사용자입니다."));

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ReserveException("존재하지 않는 학교입니다."));

        LocalDate startDate = requestDTO.getStartDate();

        // 2. 타입별 분기
        if (reserveType == ReserveType.PLACE) {
            return createPlaceReserve(user, school, startDate);
        } else {
            return createParkingReserve(user, school, startDate);
        }
    }

    /* ================= PLACE 예약 (하루 1팀) ================= */

    private ReserveCreateResponseDTO createPlaceReserve(
            User user,
            School school,
            LocalDate date
    ) {
        // PENDING + COMPLETED 모두 차단 (정책: 결제 대기 중/완료 모두 1팀 제한)
        boolean exists = reserveRepository.existsBySchoolIdAndReserveTypeAndReserveStatusInAndStartDate(
                school.getId(),
                ReserveType.PLACE,
                List.of(ReserveStatus.PENDING, ReserveStatus.COMPLETED),
                date
        );

        if (exists) {
            throw new ReserveException("이미 해당 날짜에 장소대여 예약이 존재합니다.");
        }

        // 결제 전 → PENDING
        Reserve reserve = Reserve.builder()
                .user(user)
                .school(school)
                .reserveType(ReserveType.PLACE)
                .reserveStatus(ReserveStatus.PENDING)
                .startDate(date)
                .endDate(date)
                .reservePrice(50_000)
                .reserveDeposit(50_000)
                .build();

        Reserve saved = reserveRepository.save(reserve);

        return ReserveCreateResponseDTO.builder()
                .reserveId(saved.getId())
                .reserveStatus(saved.getReserveStatus())
                .price(saved.getReservePrice())
                .deposit(saved.getReserveDeposit())
                .build();
    }

    /* ================= PARKING 예약 (선착순 + 대기) ================= */

    private ReserveCreateResponseDTO createParkingReserve(
            User user,
            School school,
            LocalDate date
    ) {
        // 학교 대지면적 기반 최대 수용량 계산 (정수로 내림)
        Double land = school.getSchoolLand();
        if (land == null || land <= 0) {
            throw new ReserveException("해당 학교의 주차 수용 정보를 계산할 수 없습니다.");
        }

        int maxCapacity = (int) Math.floor(land / PARKING_AREA_PER_CAR);
        if (maxCapacity <= 0) {
            throw new ReserveException("해당 학교는 주차 수용이 불가능합니다.");
        }

        // 활성(완료) 주차 예약 수
        long activeCount = getCompletedParkingCount(school.getId(), date);

        Reserve reserve;

        if (activeCount < maxCapacity) {
            // 자리 있음 → PENDING
            reserve = Reserve.builder()
                    .user(user)
                    .school(school)
                    .reserveType(ReserveType.PARKING)
                    .reserveStatus(ReserveStatus.PENDING)
                    .startDate(date)
                    .endDate(date.plusMonths(1))
                    .reservePrice(30_000)
                    .reserveDeposit(0)
                    .build();
        } else {
            // 자리 없음 → WAITING
            Integer maxOrder = reserveRepository.findMaxWaitingOrder(school.getId(), date);
            int nextOrder = (maxOrder == null) ? 1 : (maxOrder + 1);

            reserve = Reserve.builder()
                    .user(user)
                    .school(school)
                    .reserveType(ReserveType.PARKING)
                    .reserveStatus(ReserveStatus.WAITING)
                    .waitingOrder(nextOrder)
                    .startDate(date)
                    .endDate(date.plusMonths(1))
                    .reservePrice(30_000)
                    .reserveDeposit(0)
                    .build();
        }

        Reserve saved = reserveRepository.save(reserve);

        return ReserveCreateResponseDTO.builder()
                .reserveId(saved.getId())
                .reserveStatus(saved.getReserveStatus())
                .price(saved.getReservePrice())
                .deposit(saved.getReserveDeposit())
                .waitingOrder(saved.getWaitingOrder())
                .build();
    }

    /* ================= 유저 제한 ================= */

    private void validateParkingLimit(Long userId) {
        // 정책: 주차는 "진행 중(PENDING) + 완료(COMPLETED)" 합쳐서 1건만 허용
        boolean exists = reserveRepository.existsByUserIdAndReserveTypeAndReserveStatusIn(
                userId,
                ReserveType.PARKING,
                List.of(ReserveStatus.PENDING, ReserveStatus.COMPLETED)
        );

        if (exists) {
            throw new ReserveException("주차 예약은 1건만 가능합니다. 취소 후 다시 예약해주세요.");
        }
    }

    private void validatePlaceLimit(Long userId) {
        // 정책: 장소대여는 "진행 중(PENDING) + 완료(COMPLETED)" 합쳐서 최대 2건
        long count = reserveRepository.countByUserIdAndReserveTypeAndReserveStatusIn(
                userId,
                ReserveType.PLACE,
                List.of(ReserveStatus.PENDING, ReserveStatus.COMPLETED)
        );

        if (count >= 2) {
            throw new ReserveException("장소대여는 최대 2건까지 예약 가능합니다.");
        }
    }

    public long getCompletedParkingCount(Long schoolId, LocalDate date) {
        return reserveRepository.countActiveParking(
                schoolId,
                ReserveType.PARKING,
                ReserveStatus.COMPLETED,
                date
        );
    }

    /* ================= 예약 취소 ================= */

    // 예약 취소 (WAITING / PENDING / COMPLETED)
    public void cancelReserve(Long reserveId) {
        Reserve target = reserveRepository.findById(reserveId)
                .orElseThrow(() -> new ReserveException("예약 없음"));

        ReserveStatus status = target.getReserveStatus();

        if (status == ReserveStatus.WAITING) {
            cancelWaitingReserve(target);
            return;
        }

        if (status == ReserveStatus.PENDING || status == ReserveStatus.COMPLETED) {
            cancelActiveReserve(target);
            return;
        }

        throw new ReserveException("취소할 수 없는 상태입니다.");
    }

    // WAITING 예약 취소 → 대기번호 재정렬
    private void cancelWaitingReserve(Reserve target) {
        Long schoolId = target.getSchool().getId();
        LocalDate date = target.getStartDate();
        Integer order = target.getWaitingOrder();

        if (order == null) {
            // WAITING인데 order가 없으면 데이터 이상
            throw new ReserveException("대기번호 정보가 없습니다.");
        }

        // 상태 변경
        target.setReserveStatus(ReserveStatus.CANCELED);
        target.setWaitingOrder(null);

        // 뒤에 있던 대기자 조회 (PARKING + WAITING)
        List<Reserve> afterList = reserveRepository.findWaitingAfterOrder(
                schoolId,
                ReserveType.PARKING,
                ReserveStatus.WAITING,
                date,
                order
        );

        // 대기번호 1씩 당김
        for (Reserve r : afterList) {
            if (r.getWaitingOrder() != null) {
                r.setWaitingOrder(r.getWaitingOrder() - 1);
            }
        }
    }

    // PENDING / COMPLETED 예약 취소 → 자리 하나 비는 상태
    private void cancelActiveReserve(Reserve target) {
        target.setReserveStatus(ReserveStatus.CANCELED);
    }

    public void cancelReserve(Long reserveId, Long userId) {
        Reserve reserve = reserveRepository.findById(reserveId)
                .orElseThrow();

        reserve.cancel();
    }

}
