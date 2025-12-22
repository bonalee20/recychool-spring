package com.app.recychool.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class BulkLoadServiceTest {

    @Autowired
    private BulkLoadService bulkLoadService;

    @Test
    void loadFromClasspathPolygons() throws Exception{
        bulkLoadService.loadFromClasspathPolygons(20);
    }
}