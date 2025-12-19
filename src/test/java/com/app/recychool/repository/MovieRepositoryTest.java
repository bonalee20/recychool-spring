package com.app.recychool.repository;

import com.app.recychool.domain.entity.Movie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Commit
@Slf4j
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    public void testdata(){

        Movie movie1 = new Movie();
        movie1.setMovieTime("18:00~ 19:41");
        movie1.setMoviePeopleAll(30);
        movie1.setMovieTitle("주토피아");
        movieRepository.save(movie1);

        Movie movie2 = new Movie();
        movie2.setMovieTime("18:00~ 19:41");
        movie2.setMoviePeopleAll(30);
        movie2.setMovieTitle("car");
        movieRepository.save(movie2);
    }

}
