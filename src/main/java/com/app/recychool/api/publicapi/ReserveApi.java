package com.app.recychool.api.publicapi;

import com.app.recychool.domain.dto.ApiResponseDTO;
import com.app.recychool.domain.dto.reserve.SchoolReservePageResponseDTO;
import com.app.recychool.domain.enums.ReserveType;
import com.app.recychool.service.ReserveQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController("publicReserveApi")
@RequestMapping("/api/public/schools")
@RequiredArgsConstructor
public class ReserveApi {

    private final ReserveQueryService reserveQueryService;

    // 장소대여 페이지
    @GetMapping("/{schoolId}/place")
    public ResponseEntity<ApiResponseDTO<SchoolReservePageResponseDTO>> getPlaceReservePage(
            @PathVariable Long schoolId
    ) {
        SchoolReservePageResponseDTO response =
                reserveQueryService.getReservePageInfo(
                        schoolId,
                        ReserveType.PLACE
                );

        return ResponseEntity.ok(
                ApiResponseDTO.of("장소대여 페이지 조회 성공", response)
        );
    }

    // 주차 예약 페이지
    @GetMapping("/{schoolId}/parking")
    public ResponseEntity<ApiResponseDTO<SchoolReservePageResponseDTO>> getParkingReservePage(
            @PathVariable Long schoolId
    ) {
        SchoolReservePageResponseDTO response =
                reserveQueryService.getReservePageInfo(
                        schoolId,
                        ReserveType.PARKING
                );

        return ResponseEntity.ok(
                ApiResponseDTO.of("주차 예약 페이지 조회 성공", response)
        );
    }

    @GetMapping("/{schoolId}/parking/counts")
    public ResponseEntity<ApiResponseDTO<Map<LocalDate, Integer>>> getParkingCounts(
            @PathVariable Long schoolId
    ) {
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusMonths(1);

        Map<LocalDate, Integer> result =
                reserveQueryService.getParkingCountMap(schoolId, today, end);

        return ResponseEntity.ok(
                ApiResponseDTO.of("주차 날짜별 예약 수 조회 성공", result)
        );
    }

}


