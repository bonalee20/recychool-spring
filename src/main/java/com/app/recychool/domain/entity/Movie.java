package com.app.recychool.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_MOVIE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "SEQ_MOVIE_GENERATOR",
        sequenceName = "SEQ_MOVIE",
        allocationSize = 1
)
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_MOVIE_GENERATOR")
    private Long id;
    private String MovieTitle;
    private String MoviePeople;
    private String MoviePeopleAll;
    private String MovieName;

}
