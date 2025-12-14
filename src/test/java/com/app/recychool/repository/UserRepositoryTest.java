package com.app.recychool.repository;

import com.app.recychool.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Commit
@Slf4j
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    @Test
    public void userSaveTest() {
        User user = new User();
        user.setUserEmail("test123@gmail.com");
        user.setUserName("test123");
        user.setUserPassword("test123!@#");
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        userRepository.save(user);
    }
}