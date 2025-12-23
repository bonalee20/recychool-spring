package com.app.recychool.service;

import com.app.recychool.domain.dto.MovieDTO;

import java.util.List;
import java.util.Map;

public interface MovieService {
    List<MovieDTO> getMoviesThisMonth();

}