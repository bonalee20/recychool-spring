package com.app.recychool.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "TBL_MOVIE_RESERVATION")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "SEQ_MOVIE_RESERVATION_GENERATOR",
        sequenceName = "SEQ_MOVIE_RESERVATION",
        allocationSize = 1
)
public class MovieReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_MOVIE_RESERVATION_GENERATOR")
    private Long id;
    private Date movieReservationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MOVIE_ID")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHOOL_ID")
    private School school;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;


}
