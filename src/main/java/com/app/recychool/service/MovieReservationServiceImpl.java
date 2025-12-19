package com.app.recychool.service;

import com.app.recychool.domain.entity.MovieReservation;
import com.app.recychool.repository.MovieReservationRepository;
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

    @Override
    public Map<String, Long> save(MovieReservation movieReservation) {
        Map<String, Long> response = new HashMap<>();
        MovieReservation savedReservation = movieReservationRepository.save(movieReservation);
        response.put("newReservationId", savedReservation.getId());
        return response;
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
}
