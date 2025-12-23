package com.app.recychool.api.privateapi;

import com.app.recychool.domain.dto.ApiResponseDTO;
import com.app.recychool.domain.dto.MovieDTO;
import com.app.recychool.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/movies") // 영화 관련 URL 프리픽스
public class MovieApi {

    private final MovieService movieService;

    @GetMapping("/this-month")
    public List<MovieDTO> getMoviesThisMonth() {
        return movieService.getMoviesThisMonth();
    }
}