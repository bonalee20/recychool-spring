package com.app.recychool.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LargeClobInsertServiceTest {

    @Autowired
    private LargeClobInsertService largeClobInsertService;

    @Test
    void runBulkLoad() {
        List<File> files = List.of(
                new File("src/main/resources/polygons/seoul_polygon_5179.geojson"),
                new File("src/main/resources/polygons/gyeonggi_polygon_5179.geojson")
        );
        largeClobInsertService.insertFilesInBatches(files, 1);
    }

}