package com.app.recychool.repository;

import com.app.recychool.domain.entity.Reserve;
import com.app.recychool.domain.entity.School;
import com.app.recychool.domain.enums.ReserveStatus;
import com.app.recychool.domain.enums.ReserveType;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve, Long> {

    // PLACE: 하루 1팀 (PENDING + CONFIRMED 차단)
    public boolean existsBySchoolIdAndReserveTypeAndReserveStatusInAndStartDate(
            Long schoolId,
            ReserveType reserveType,
            List<ReserveStatus> statuses,
            LocalDate startDate
    );

    // PARKING: 활성 예약 수 (PENDING + CONFIRMED)
    public long countBySchoolIdAndReserveTypeAndReserveStatusInAndStartDate(
            Long schoolId,
            ReserveType reserveType,
            List<ReserveStatus> statuses,
            LocalDate startDate
    );

    // PARKING: 대기번호 계산
    @Query("""
        SELECT MAX(r.waitingOrder)
        FROM Reserve r
        WHERE r.school.id = :schoolId
          AND r.reserveType = com.app.recychool.domain.enums.ReserveType.PARKING
          AND r.startDate = :startDate
    """)
    public Integer findMaxWaitingOrder(Long schoolId, LocalDate startDate);

    // 유저 제한

    // 주차: 유저 1건 제한
    public boolean existsByUserIdAndReserveTypeAndReserveStatusIn(
            Long userId,
            ReserveType reserveType,
            List<ReserveStatus> statuses
    );

    // 장소대여: 유저 최대 2건
    public long countByUserIdAndReserveTypeAndReserveStatusIn(
            Long userId,
            ReserveType reserveType,
            List<ReserveStatus> statuses
    );

    public List<Reserve> findBySchoolIdAndReserveTypeAndReserveStatus(
            Long schoolId,
            ReserveType reserveType,
            ReserveStatus reserveStatus
    );

    public long countBySchoolIdAndReserveTypeAndReserveStatusAndStartDate(
            Long schoolId,
            ReserveType reserveType,
            ReserveStatus reserveStatus,
            LocalDate startDate
    );

    @Query("""
    SELECT COUNT(r)
    FROM Reserve r
    WHERE r.school.id = :schoolId
      AND r.reserveType = :type
      AND r.reserveStatus = :status
      AND :date BETWEEN r.startDate AND r.endDate
    """)
    public long countActiveParking(
            @Param("schoolId") Long schoolId,
            @Param("type") ReserveType type,
            @Param("status") ReserveStatus status,
            @Param("date") LocalDate date
    );

    // 특정 날짜 대기열 전체
    @Query("""
    SELECT r
    FROM Reserve r
    WHERE r.school.id = :schoolId
      AND r.reserveType = :reserveType
      AND r.reserveStatus = :reserveStatus
      AND r.startDate = :date
    ORDER BY r.waitingOrder ASC
    """)
    public List<Reserve> findWaitingQueue(
            @Param("schoolId") Long schoolId,
            @Param("reserveType") ReserveType reserveType,
            @Param("reserveStatus") ReserveStatus reserveStatus,
            @Param("date") LocalDate date
    );

    // 특정 waitingOrder 뒤에 있는 대기자들
    @Query("""
        SELECT r
        FROM Reserve r
        WHERE r.school.id = :schoolId
          AND r.reserveType = :reserveType
          AND r.reserveStatus = :reserveStatus
          AND r.startDate = :date
          AND r.waitingOrder > :order
        ORDER BY r.waitingOrder ASC
    """)

    public List<Reserve> findWaitingAfterOrder(
            @Param("schoolId") Long schoolId,
            @Param("reserveType") ReserveType reserveType,
            @Param("reserveStatus") ReserveStatus reserveStatus,
            @Param("date") LocalDate date,
            @Param("order") Integer order
    );



}
