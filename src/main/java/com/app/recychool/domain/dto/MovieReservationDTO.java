package com.app.recychool.domain.dto;


import com.app.recychool.domain.entity.Movie;
import com.app.recychool.domain.entity.MovieReservation;
import com.app.recychool.domain.entity.School;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieReservationDTO {

    private Long id;
    private Date movieReservationDate;
    private School school;
    private Movie movie;

    //private Long userId;


    public MovieReservation toEntity() {
        MovieReservation movieReservation = MovieReservation.builder()
                .id(id)
                .movieReservationDate(movieReservationDate)
                .school(school)
                .movie(movie)
                .build();
        return movieReservation;
    }

}
