package com.app.recychool.api.publicapi;

import com.app.recychool.domain.dto.ApiResponseDTO;
import com.app.recychool.domain.dto.reserve.SchoolReservePageResponseDTO;
import com.app.recychool.domain.entity.School;
import com.app.recychool.domain.enums.ReserveType;
import com.app.recychool.service.ReserveQueryService;
import com.app.recychool.service.SchoolCoodinateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/schools")
public class ReserveApi {

    private final ReserveQueryService reserveQueryService;
    private final SchoolCoodinateService schoolCoodinateService;

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

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDTO.of("장소대여 페이지 조회 성공", response));
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

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDTO.of("주차 예약 페이지 조회 성공", response));
    }

    // 주차 날짜별 예약 수

    @GetMapping("/{schoolId}/parking/counts")
    public ResponseEntity<ApiResponseDTO<Map<String, Integer>>> getParkingCounts(
            @PathVariable Long schoolId
    ) {
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusMonths(1);

        Map<String, Integer> result =
                reserveQueryService.getParkingCountMap(schoolId, today, end);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDTO.of("주차 날짜별 예약 수 조회 성공", result));
    }

    //  학교 좌표 조회
    @GetMapping("/{schoolId}/coordinate")
    public ResponseEntity<ApiResponseDTO<Map<String, Double>>> getSchoolCoordinate(
            @PathVariable Long schoolId
    ) {
        School school = schoolCoodinateService.getSchoolCoordinate(schoolId);

        Map<String, Double> coord = Map.of(
                "lat", school.getSchoolLat(),
                "lng", school.getSchoolLon()
        );

        return ResponseEntity.ok(
                ApiResponseDTO.of("학교 좌표 조회 성공", coord)
        );
    }
}
