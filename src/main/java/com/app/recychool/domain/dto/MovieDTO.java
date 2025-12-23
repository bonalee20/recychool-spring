package com.app.recychool.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MovieDTO {
    private String movieTitle;
    private String movieTime;      // 예: "120분" or "02:00"
    private Integer moviePeopleAll; // 전체 관람 인원

    private LocalDateTime screeningDate;
}