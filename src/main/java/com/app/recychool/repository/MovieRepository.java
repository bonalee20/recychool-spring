package com.app.recychool.repository;


import com.app.recychool.domain.entity.Movie;
import com.app.recychool.domain.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    public Movie save(Movie movie);

}
