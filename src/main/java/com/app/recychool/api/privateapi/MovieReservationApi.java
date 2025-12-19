package com.app.recychool.controller;

import com.app.recychool.domain.dto.ApiResponseDTO; // DTO 위치에 맞게 수정 필요
import com.app.recychool.domain.entity.MovieReservation;
import com.app.recychool.service.MovieReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class MovieReservationApi {

    private final MovieReservationService movieReservationService;

    @PostMapping("/write")
    public ResponseEntity<ApiResponseDTO> write(@RequestBody MovieReservation movieReservation) {
        Map<String, Long> response = movieReservationService.save(movieReservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.of("예약 성공", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> delete(@PathVariable Long id) {
        movieReservationService.delete(id);
        return ResponseEntity.ok(ApiResponseDTO.of("예약 취소 성공", null));
    }

    // 학교별 잔여 좌석
    @GetMapping("/count/{schoolId}")
    public ResponseEntity<ApiResponseDTO> getCount(@PathVariable Long schoolId) {
        long count = movieReservationService.getCountBySchoolId(schoolId);
        return ResponseEntity.ok(ApiResponseDTO.of("잔여 좌석 조회 성공", count));
    }

    // 마이예약
    @GetMapping("/my/{userId}")
    public ResponseEntity<ApiResponseDTO> getMyReservations(@PathVariable Long userId) {
        List<MovieReservation> reservations = movieReservationService.getMyReservations(userId);
        return ResponseEntity.ok(ApiResponseDTO.of("내 예약 목록 조회 성공", reservations));
    }
}