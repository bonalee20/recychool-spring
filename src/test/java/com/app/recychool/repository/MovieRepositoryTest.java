package com.app.recychool.repository;

import com.app.recychool.domain.entity.Movie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@Commit
@Slf4j
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    public void testdata3(){
        Movie movie1 = new Movie();
        movie1.setMovieTime("18:00~ 19:41");
        movie1.setMoviePeopleAll(30);
        movie1.setMovieTitle("코렐라인");
        movie1.setMovieStartDate(LocalDateTime.of(2025, 12, 18, 0, 0, 0));        movieRepository.save(movie1);
        movieRepository.save(movie1);
    }

    @Test
    public void testdata2(){
        Movie movie1 = new Movie();
        movie1.setMovieTime("18:00~ 19:41");
        movie1.setMoviePeopleAll(30);
        movie1.setMovieTitle("주토피아");
        movie1.setMovieStartDate(LocalDateTime.of(2026, 2, 21, 0, 0, 0));        movieRepository.save(movie1);
        movieRepository.save(movie1);
    }


}
