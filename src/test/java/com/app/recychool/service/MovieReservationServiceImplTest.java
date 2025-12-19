package com.app.recychool.service;

import com.app.recychool.repository.MovieReservationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
@Transactional
class MovieReservationServiceImplTest {
    @Autowired
    private MovieReservationService movieReservationService;

    @Test
    void save() {
    }

    @Test
    void delete() {
    }

    @Test
    void getCountBySchoolId() {
    }

    @Test
    void getMyReservations() {
    }
}