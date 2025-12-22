package com.app.recychool.service;

import com.app.recychool.domain.entity.Movie;
import com.app.recychool.domain.entity.MovieReservation;
import com.app.recychool.domain.entity.School;
import com.app.recychool.domain.entity.User;
import com.app.recychool.repository.MovieRepository;
import com.app.recychool.repository.MovieReservationRepository;
import com.app.recychool.repository.SchoolRepository;
import com.app.recychool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class MovieReservationServiceImpl implements MovieReservationService {

    private final MovieReservationRepository movieReservationRepository;
    private final MovieRepository movieRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;

    @Override
    public Map<String, Long> save(MovieReservation req) {
        Long movieId = req.getMovie().getId();
        Long schoolId = req.getSchool().getId();
        Long userId = req.getUser().getId();
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("영화 없음: " + movieId));

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("학교 없음: " + schoolId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음: " + userId));

        MovieReservation reservation = MovieReservation.builder()
                .movie(movie)
                .school(school)
                .user(user)
                .movieReservationDate(req.getMovieReservationDate())
                .build();

        MovieReservation saved = movieReservationRepository.save(reservation);
        Map<String, Long> response = new HashMap<>();
        response.put("newReservationId", saved.getId());
        return response;
    }

    @Override
    public Map<String, Long> saveByIds(Long schoolId, String movieTitle, Long userId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("학교 없음"));
        
        Movie movie = movieRepository.findAll().stream()
                .filter(m -> movieTitle.equals(m.getMovieTitle()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("영화 없음"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        
        MovieReservation reservation = MovieReservation.builder()
                .movie(movie)
                .school(school)
                .user(user)
                .movieReservationDate(new java.util.Date())
                .build();
        
        MovieReservation saved = movieReservationRepository.save(reservation);
        return Map.of("newReservationId", saved.getId());
    }

    @Override
    public void delete(Long id) {
        movieReservationRepository.deleteById(id);
    }

    @Override
    public long getCountBySchoolId(Long schoolId) {
        return movieReservationRepository.countBySchoolId(schoolId);
    }

    @Override
    public List<MovieReservation> getMyReservations(Long userId) {
        return movieReservationRepository.findMyMovieReservation(userId);
    }
    @Override
    public List<School> getMovieSchools() {
        return movieReservationRepository.findMovieSchoolNames();
    }
}
