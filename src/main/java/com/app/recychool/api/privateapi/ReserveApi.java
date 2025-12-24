package com.app.recychool.api.privateapi;

import com.app.recychool.domain.dto.ApiResponseDTO;
import com.app.recychool.domain.dto.reserve.ReserveCreateRequestDTO;
import com.app.recychool.domain.dto.reserve.ReserveCreateResponseDTO;
import com.app.recychool.domain.enums.ReserveStatus;
import com.app.recychool.domain.enums.ReserveType;
import com.app.recychool.service.AuthService;
import com.app.recychool.service.ReserveService;
import com.app.recychool.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;

@RestController("privateReserveApi")
@RequestMapping("/private/schools")
@RequiredArgsConstructor
public class ReserveApi {

    private final ReserveService reserveService;
    private final AuthService authService;
    private final UserService userService;

    // 장소대여 예약
    @PostMapping("/{schoolId}/place/reserves")
    public ResponseEntity<ApiResponseDTO<ReserveCreateResponseDTO>> reservePlace(
            Authentication authentication,
            @PathVariable Long schoolId,
            @RequestBody ReserveCreateRequestDTO requestDTO
    ) {
        Long userId = getUserId(authentication);

        ReserveCreateResponseDTO response =
                reserveService.createReserve(
                        userId,
                        schoolId,
                        ReserveType.PLACE,
                        requestDTO
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.of("장소대여 예약 완료", response));
    }

    // 주차 예약
    @PostMapping("/{schoolId}/parking/reserves")
    public ResponseEntity<ApiResponseDTO<ReserveCreateResponseDTO>> reserveParking(
            Authentication authentication,
            @PathVariable Long schoolId,
            @RequestBody ReserveCreateRequestDTO requestDTO
    ) {
        Long userId = getUserId(authentication);

        ReserveCreateResponseDTO response =
                reserveService.createReserve(
                        userId,
                        schoolId,
                        ReserveType.PARKING,
                        requestDTO
                );

        // WAITING → 202
        if (response.getReserveStatus() == ReserveStatus.WAITING) {
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(ApiResponseDTO.of("주차 예약 대기 등록", response));
        }

        // CONFIRMED → 200
        return ResponseEntity
                .ok(ApiResponseDTO.of("주차 예약 확정", response));
    }

    // 공통 유저 추출
    private Long getUserId(Authentication authentication) {
        String userEmail =
                authService.getUserEmailFromAuthentication(authentication);

        return userService.getUserIdByUserEmail(userEmail);
    }

    // 예약 취소
    @DeleteMapping("/reserves/{reserveId}")
    public ResponseEntity<ApiResponseDTO<Void>> cancelReserve(
            @PathVariable Long reserveId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);

        reserveService.cancelReserve(reserveId, userId);
        return ResponseEntity.ok(ApiResponseDTO.of("예약 취소 완료"));
    }

    // 내 장소 예약 ID 목록 (최대 2개)
    @GetMapping("/place/reserves")
    public ResponseEntity<ApiResponseDTO<?>> getMyPlaceReserves(
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                ApiResponseDTO.of(
                        "내 장소 예약 조회 성공",
                        reserveService.getMyPlaceReserves(userId)
                )
        );
    }

    // 내 주차 예약 ID (1개)
    @GetMapping("/parking/reserves")
    public ResponseEntity<ApiResponseDTO<?>> getMyParkingReserve(
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);

        Long reserveId = reserveService.getMyParkingReserve(userId);

        return ResponseEntity.ok(
                ApiResponseDTO.of("내 주차 예약 조회 성공", reserveId)
        );
    }


}

