package com.app.recychool.service;

import com.app.recychool.domain.dto.reserve.SchoolReservePageResponseDTO;
import com.app.recychool.domain.enums.ReserveType;

import java.time.LocalDate;
import java.util.Map;

public interface ReserveQueryService {

    // 예약 페이지 진입 시 호출
    SchoolReservePageResponseDTO getReservePageInfo(
            Long schoolId,
            ReserveType reserveType
    );

    Map<String, Integer> getParkingCountMap(
            Long schoolId,
            LocalDate from,
            LocalDate to
    );
}
