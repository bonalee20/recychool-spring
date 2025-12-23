package com.app.recychool.service;

import com.app.recychool.domain.dto.reserve.SchoolReservePageResponseDTO;
import com.app.recychool.domain.entity.Reserve;
import com.app.recychool.domain.entity.School;
import com.app.recychool.domain.enums.ReserveStatus;
import com.app.recychool.domain.enums.ReserveType;
import com.app.recychool.repository.ReserveRepository;
import com.app.recychool.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReserveQueryServiceImpl implements ReserveQueryService {

    private final SchoolRepository schoolRepository;
    private final ReserveRepository reserveRepository;
    private static final int PARKING_AREA_PER_CAR = 100;

    @Override
    public SchoolReservePageResponseDTO getReservePageInfo(
            Long schoolId,
            ReserveType reserveType
    ) {

        // 1. 학교 조회
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalStateException("학교가 존재하지 않습니다."));

        // 2. 예약 불가 날짜 계산
        List<LocalDate> unavailableDates =
                getUnavailableDates(schoolId, reserveType);

        // 3. 타입별 정책 값
        int price;
        int deposit;
        String usageTime;

        Double maxParkingCapacity = null;

        if (reserveType == ReserveType.PLACE) {
            price = 0;
            deposit = 50_000;
            usageTime = "09:00 ~ 17:00";
        } else {
            price = 30_000;
            deposit = 0;
            usageTime = "18:00 ~ 08:00";

            // ✔ 주차 최대 수용 인원 (면적 / 100, 소수점 버림)
            maxParkingCapacity = school.getSchoolArea() / PARKING_AREA_PER_CAR;
        }

        // 4. DTO 반환
        return SchoolReservePageResponseDTO.builder()
                .schoolId(school.getId())
                .schoolName(school.getSchoolName())
                .schoolAddress(school.getSchoolAddress())
                .schoolPhone(school.getSchoolPhone())

                .schoolArea(school.getSchoolArea())
                .schoolImageName(school.getSchoolImageName())
                .schoolImagePath(school.getSchoolImagePath())

                .reserveType(reserveType.name())
                .usageTime(usageTime)
                .price(price)
                .deposit(deposit)

                .maxParkingCapacity(maxParkingCapacity)
                .unavailableDates(unavailableDates)
                .build();
    }

    // PLACE 예약 불가 날짜
    private List<LocalDate> getUnavailableDates(
            Long schoolId,
            ReserveType reserveType
    ) {

        if (reserveType == ReserveType.PLACE) {
            return reserveRepository
                    .findBySchoolIdAndReserveTypeAndReserveStatus(
                            schoolId,
                            ReserveType.PLACE,
                            ReserveStatus.COMPLETED
                    )
                    .stream()
                    .map(Reserve::getStartDate)
                    .toList();
        }

        // PARKING: 날짜 제한 없음 (수용 인원으로만 제어)
        return List.of();
    }

    @Override
    public Map<String, Integer> getParkingCountMap(
            Long schoolId,
            LocalDate from,
            LocalDate to
    ) {
        Map<String, Integer> result = new HashMap<>();

        LocalDate date = from;
        while (!date.isAfter(to)) {
            long count = reserveRepository.countActiveParking(
                    schoolId,
                    ReserveType.PARKING,
                    ReserveStatus.COMPLETED,
                    date
            );
            result.put(date.toString(), (int) count);
            date = date.plusDays(1);
        }

        return result;
    }

}
