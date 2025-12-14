package com.app.recychool.api.publicapi;

import com.app.recychool.domain.dto.ApiResponseDTO;
import com.app.recychool.domain.entity.User;
import com.app.recychool.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/*")
public class UserApi {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO> register(@RequestBody User user){
        String encodedPassword = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(encodedPassword);
        userService.register(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.of("회원가입이 완료되었습니다")); // 201
    }

    //  이메일 찾기
    @PostMapping("/find-email")
    public ResponseEntity<ApiResponseDTO> findemail(@RequestBody User user){
        List<String> userEmailList = userService.getUserEmailsByNameAndPhone(user);
        return ResponseEntity.ok(ApiResponseDTO.of("이메일 찾기 완료", userEmailList));
    }
}
