package com.app.recychool.service;

import com.app.recychool.domain.entity.MovieReservation;

import java.util.List;
import java.util.Map;

public interface MovieReservationService {
    // 저장 (예약하기) - 리턴 타입 변경
    public Map<String, Long> save(MovieReservation movieReservation);

    // 삭제 (예약 취소)
    public void delete(Long id);

    // 학교별 예매 수 확인
    public long getCountBySchoolId(Long schoolId);

    // 마이 예약 확인
    public List<MovieReservation> getMyReservations(Long userId);
}
