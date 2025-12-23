package com.app.recychool.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TBL_MOVIE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "movieReservations")
@SequenceGenerator(
        name = "SEQ_MOVIE_GENERATOR",
        sequenceName = "SEQ_MOVIE",
        allocationSize = 1
)
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_MOVIE_GENERATOR")
    private Long id;
    private String movieTitle;
    private String movieTime;
    private Integer moviePeopleAll;
    private LocalDateTime movieStartDate; // 상영 날짜

    @OneToMany(mappedBy = "movie")
    @JsonIgnore
    private List<MovieReservation> movieReservations;

}
