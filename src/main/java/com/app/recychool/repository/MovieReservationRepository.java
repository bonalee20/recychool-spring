package com.app.recychool.repository;

import com.app.recychool.domain.entity.MovieReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieReservationRepository extends JpaRepository<MovieReservation, Long> {
    // save & deleteById 기본
    // 마이 예약 -> 유저 수정 필요
    @Query("select mr from MovieReservation mr " +
            "join fetch mr.movie m " +
            "join fetch mr.school s " +
            "where mr.user.id = :userId " +
            "order by mr.movieReservationDate desc")
    public List<MovieReservation> findMyMovieReservation(Long userId);

    // 잔여 좌석 확인용
    public long countBySchoolId(Long schoolId);

}
