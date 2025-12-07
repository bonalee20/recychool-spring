package com.app.recychool.api.privateapi;

import com.app.recychool.domain.dto.ApiResponseDTO;
import com.app.recychool.domain.dto.UserResponseDTO;
import com.app.recychool.exception.UserException;
import com.app.recychool.service.AuthService;
import com.app.recychool.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private/users")
@Slf4j
public class UserAuthApi {
    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO> me(Authentication authentication) {
        UserResponseDTO currentUser = getUserByToken(authentication);
        currentUser.setUserPassword(null);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDTO.of("내 정보 조회 성공", currentUser));
    }

    private UserResponseDTO getUserByToken(Authentication authentication){
        String email = authService.getUserEmailFromAuthentication(authentication);
        if (email == null || email.isBlank()) {
            throw new UserException("인증 정보에 이메일이 없습니다.");
        }
        Long userId = userService.getUserIdByUserEmail(email);
        UserResponseDTO currentUser = userService.getUserById(userId);
        return currentUser;
    }

}
