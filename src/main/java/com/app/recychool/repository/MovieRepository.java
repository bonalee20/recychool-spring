package com.app.recychool.repository;


import com.app.recychool.domain.dto.MovieDTO;
import com.app.recychool.domain.entity.Movie;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {


    @Query("SELECT new com.app.recychool.domain.dto.MovieDTO(" +
            "    m.movieTitle, " +
            "    m.movieTime, " +
            "    m.moviePeopleAll, " +
            "    m.movieStartDate) " +  // Entity의 날짜 필드를 DTO의 screeningDate에 매핑
            "FROM Movie m " +
            "WHERE m.movieStartDate BETWEEN :start AND :end " +
            "ORDER BY m.movieStartDate ASC")
    public List<MovieDTO> findMoviesThisMonth(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    Optional<Movie> findByMovieTitle(String movieTitle);
    Optional<Movie> findTopByMovieStartDateGreaterThanEqualOrderByMovieStartDateAsc(LocalDateTime date);
}
