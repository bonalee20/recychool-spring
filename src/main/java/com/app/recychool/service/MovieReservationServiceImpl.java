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

        // ✅ 1) 프론트에서 받은 건 id만 사용한다
        Long movieId = req.getMovie().getId();
        Long schoolId = req.getSchool().getId();
        Long userId = req.getUser().getId();

        // ✅ 2) DB에서 진짜 엔티티를 조회한다 (없으면 에러)
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("영화 없음: " + movieId));

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("학교 없음: " + schoolId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음: " + userId));

        // ✅ 3) 새 예약 엔티티를 만든다 (프론트가 보낸 다른 값은 무시됨)
        MovieReservation reservation = MovieReservation.builder()
                .movie(movie)
                .school(school)
                .user(user)
                .movieReservationDate(req.getMovieReservationDate())
                .build();

        // ✅ 4) 저장
        MovieReservation saved = movieReservationRepository.save(reservation);

        // ✅ 5) 응답
        Map<String, Long> response = new HashMap<>();
        response.put("newReservationId", saved.getId());
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
