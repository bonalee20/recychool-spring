package com.app.recychool.repository;

import com.app.recychool.domain.entity.Polygons;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j

class PolygonsRepositoryTest {

    @Autowired
    private PolygonsRepository polygonsRepository;

    @Test
    void findAllGeomsAsGeoJson() {
        List<Polygons> polygons = polygonsRepository.findAll();
        log.info("polygons size: {}", polygons);
    }
}