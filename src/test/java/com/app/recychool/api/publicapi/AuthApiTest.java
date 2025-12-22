package com.app.recychool.api.publicapi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Commit
@Slf4j
class AuthApiTest {
    @Autowired
    AuthApi authApi;

    @Test
    void getUserById() {
        log.info(authApi.existsCheckEmail("test1@gmail.com").toString());
    }
}