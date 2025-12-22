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

        User user = users.stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("tbl_user 데이터가 없습니다"));
        Movie movie = movies.stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("tbl_movie 데이터가 없습니다"));

        School school = schools.stream()
                .filter(s -> "영평초".equals(s.getSchoolName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("학교명=영평초 데이터가 없습니다"));

        MovieReservation reservation1 = MovieReservation.builder()
                .movie(movie)
                .school(school)
                .movieReservationDate(new Date())
                .user(user)
                .build();

        movieReservationRepository.save(reservation1);
    }

    @Test
    @Rollback(false)
    public void savetest12() {
        List<User> users = userRepository.findAll();
        List<Movie> movies = movieRepository.findAll();
        List<School> schools = schoolRepository.findAll();

        User user = users.stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("tbl_user 데이터가 없습니다"));
        Movie movie = movies.stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("tbl_movie 데이터가 없습니다"));

        School school = schools.stream()
                .filter(s -> "덕수고등학교(행당분교)".equals(s.getSchoolName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("덕수고등학교(행당분교) 데이터가 없습니다"));

        MovieReservation reservation1 = MovieReservation.builder()
                .movie(movie)
                .school(school)
                .movieReservationDate(new Date())
                .user(user)
                .build();

        movieReservationRepository.save(reservation1);
    }

    @Test
    @Rollback(false)
    public void saveMovieScheduleDummies() {
        List<Movie> movies = movieRepository.findAll();
        List<School> schools = schoolRepository.findAll();

        Movie movie = movies.get(1); // 원하는 영화

        String[] targetNames = {"영평초", "덕수고등학교(행당분교)", "구.백성초"};

        for (String name : targetNames) {
            School targetSchool = schools.stream()
                    .filter(s -> name.equals(s.getSchoolName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("학교 없음: " + name));

            MovieReservation schedule = MovieReservation.builder()
                    .movie(movie)
                    .school(targetSchool)
                    .movieReservationDate(new Date()) // 행사 날짜(원하는 날짜로 바꾸면 됨)
                    .user(null)
                    .build();

            movieReservationRepository.save(schedule);
            log.info("행사 더미 저장 완료: {}", name);
        }
    }

//    @Test
//    public void findMovieScheduleDummies() {
//        List<String> schoolNames = movieReservationRepository.findMovieSchoolNames();
//        System.out.println("조회된 학교 목록: " + schoolNames);
//    }

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