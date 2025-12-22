package com.app.recychool.api.privateapi;

import com.app.recychool.domain.dto.ApiResponseDTO;
import com.app.recychool.domain.dto.TokenDTO;
import com.app.recychool.domain.dto.UserResponseDTO;
import com.app.recychool.domain.entity.User;
import com.app.recychool.exception.UserException;
import com.app.recychool.repository.UserRepository;
import com.app.recychool.service.AuthService;
import com.app.recychool.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private/users")
@Slf4j
public class UserAuthApi {
    private final UserService userService;
    private final AuthService authService;
    private final UserRepository userRepository;

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
    @PostMapping("logout")
    public ResponseEntity<ApiResponseDTO> logout(
            Authentication authentication,
            HttpServletRequest request
    ) {
        try {
            // 1. 현재 사용자 정보 가져오기
            UserResponseDTO currentUser = getUserByToken(authentication);
            Long userId = currentUser.getId();

            // 2. Access Token 가져오기 (Authorization 헤더에서)
            String accessToken = null;
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring(7);
            }

            // 3. Refresh Token 가져오기 (쿠키에서)
            String refreshToken = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("refreshToken".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            // 4. Access Token을 블랙리스트에 추가
            if (accessToken != null && !accessToken.isBlank()) {
                TokenDTO accessTokenDTO = new TokenDTO();
                accessTokenDTO.setUserId(userId);
                accessTokenDTO.setAccessToken(accessToken);
                authService.saveBlacklistedAccessToken(accessTokenDTO);
            }

            // 5. Refresh Token 처리
            if (refreshToken != null && !refreshToken.isBlank()) {
                TokenDTO refreshTokenDTO = new TokenDTO();
                refreshTokenDTO.setUserId(userId);
                refreshTokenDTO.setRefreshToken(refreshToken);
                
                // Refresh Token을 Redis에서 삭제
                authService.revokeRefreshToken(refreshTokenDTO);
                
                // Refresh Token을 블랙리스트에 추가
                authService.saveBlacklistedToken(refreshTokenDTO);
            }

            // 6. 사용자 로그인 상태를 false로 변경
            userService.modifyUserIsLogin(userId);

            // 7. Refresh Token 쿠키 삭제
            ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    // .secure(true)  // https면 켜기
                    .path("/")       // 로그인 때랑 동일해야 삭제됨
                    .maxAge(0)       // 즉시 만료
                    .build();

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                    .body(ApiResponseDTO.of("로그아웃 성공"));

        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDTO.of("로그아웃 처리 중 오류가 발생했습니다."));
        }
    }
}
