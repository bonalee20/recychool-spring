package com.app.recychool.service;

import com.app.recychool.domain.dto.MovieDTO;
import com.app.recychool.domain.entity.Movie;
import com.app.recychool.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    @Override
    public List<MovieDTO> getMoviesThisMonth() {
        // 1. 이번 달의 시작일 (1일 00:00:00)
        LocalDateTime start = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        // 2. 이번 달의 마지막일 (말일 23:59:59.999...)
        LocalDateTime end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);


        // 3. Repository 호출 (DTO로 바로 받기)
        return movieRepository.findMoviesThisMonth(start, end);
    }


}
