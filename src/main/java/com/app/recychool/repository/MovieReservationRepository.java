package com.app.recychool.repository;

import com.app.recychool.domain.entity.MovieReservation;
import com.app.recychool.domain.entity.School;
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

    public long countBySchoolId(Long schoolId);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);


    // 더미
    @Query("select distinct mr.school from MovieReservation mr where mr.user is null")
    public List<School> findMovieSchoolNames();
}
