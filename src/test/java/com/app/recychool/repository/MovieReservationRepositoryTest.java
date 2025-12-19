package com.app.recychool.repository;


import com.app.recychool.domain.entity.Movie;
import com.app.recychool.domain.entity.MovieReservation;
import com.app.recychool.domain.entity.School;
import com.app.recychool.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootTest
@Transactional
@Slf4j
@Commit
class MovieReservationRepositoryTest {
    @Autowired
    private MovieReservationRepository movieReservationRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Rollback(false)
    public void savetest11() {
        List<User> users = userRepository.findAll();
        List<Movie> movies = movieRepository.findAll();
        List<School> schools = schoolRepository.findAll();


        MovieReservation reservation1 = MovieReservation.builder()
                .movie(movies.get(1))
                .school(schools.get(3))
                .user(users.get(0))
                .movieReservationDate(new Date())
                .build();
        MovieReservation savedReservation = movieReservationRepository.save(reservation1);

        log.info("ID : {}", savedReservation.getId());

    }

// 개수 확인
    @Test
    public void testfind(){
        movieReservationRepository.countBySchoolId(5L);
        log.info("ID : {}", movieReservationRepository.countBySchoolId(5L));
    }

    @Test
    public void testdelete(){
        movieReservationRepository.deleteById(2L);
    }

    // 마이 예약
    @Test
    public void test12() {
        List<MovieReservation> results = movieReservationRepository.findMyMovieReservation(1L);
        log.info("총 {}건의 예약이 조회되었습니다.", results.size());
    }
}