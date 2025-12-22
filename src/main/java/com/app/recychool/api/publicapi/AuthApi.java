package com.app.recychool.api.publicapi;


import com.app.recychool.domain.dto.*;
import com.app.recychool.domain.entity.User;
import com.app.recychool.exception.UserException;
import com.app.recychool.repository.UserRepository;
import com.app.recychool.service.AuthService;
import com.app.recychool.service.SmsService;
import com.app.recychool.service.UserService;
import com.app.recychool.util.DeviceIdResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/*")
public class AuthApi {

    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final UserService userService;
    private final RedisTemplate redisTemplate;
    private final SmsService smsService;
    private final UserRepository userRepository;
    private final DeviceIdResolver deviceIdResolver;
    
    // 회원 수정 (이메일 + 패스워드만 받아서 수정)
    @PostMapping("/modify")
    public ResponseEntity<ApiResponseDTO> modify(@RequestBody User user){
        // 이메일로 회원 조회
        Optional<User> foundUser = userRepository.findByUserEmail(user.getUserEmail());

        if (foundUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.of("등록된 회원이 아닙니다."));
        }

        // 찾은 회원에 패스워드 인코딩해서 설정
        User existingUser = foundUser.get();
        String encodedPassword = passwordEncoder.encode(user.getUserPassword());
        existingUser.setUserPassword(encodedPassword);
        
        // save
        userRepository.save(existingUser);
        
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("비밀번호가 변경되었습니다"));
    }

    // 로그인
    @PostMapping("login")
    public ResponseEntity<ApiResponseDTO> login(
            @RequestBody User user
    ){
        Optional<User> optUser = userRepository.findByUserEmail(user.getUserEmail());

        if (optUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDTO.of("입력한 이메일을 찾을 수 없습니다."));
        }

        User foundUser = optUser.get();

        if (!passwordEncoder.matches(user.getUserPassword(), foundUser.getUserPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDTO.of("비밀번호가 일치하지 않습니다."));
        }

        if (foundUser.getUserIsLogin() != null && foundUser.getUserIsLogin() == 1) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDTO.of("이미 로그인 되어 있습니다."));
        }
//        로그인 성공 == 로그인 했을 때 정보 가져와서 해당 유저의 아이디 비밀번호가 동일할 때 && 해당 유저의 아이디로 유저 정보 가져왔을 때 로그인이 되어 있을 때
        Map<String, String> tokens = authService.login(user);
        // refreshToken은 cookie로 전달
        // cookie: 웹 브라우저로 전송하는 단순한 문자열(세션, refreshToken)
        // XSS 탈취 위험을 방지하기 위해서 http Only로 안전하게 처리한다. 즉, JS로 접근할 수 없다.
        String refreshToken = tokens.get("refreshToken");
        ResponseCookie cookie = ResponseCookie.from("refreshToken",  refreshToken)
                .httpOnly(true) // *필수
                //      .secure(true) // https에서 사용
                .path("/") // 모든 경로에 쿠키 전송 사용
                .maxAge(60 * 60 * 24 * 7)
                .build();

        tokens.remove("refreshToken");

        // 5. redis로 교환하기 위한 key를 등록
        String key = UUID.randomUUID().toString();
        redisTemplate.opsForHash().putAll(key, tokens);
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);


        // 6. redis에 refresh 토큰을 등록 (검증)
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setUserId(user.getId());
        tokenDTO.setRefreshToken(refreshToken);
        authService.saveRefreshToken(tokenDTO);

//        리턴 직전 == 로그인 직전에 로그인 상태 true로 변경
        foundUser.setUserIsLogin(1);
        userRepository.save(foundUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponseDTO.of("로그인이 성공했습니다", tokens));
    }

    // 토큰 재발급
    @PostMapping("refresh")
    public ResponseEntity<ApiResponseDTO> refresh(@CookieValue("refreshToken") String refreshToken, @RequestBody TokenDTO tokenDTO){
        Map<String, String> response = new HashMap<String, String>();
        tokenDTO.setRefreshToken(refreshToken);
        String newAccessToken = authService.reissueAccessToken(tokenDTO);
        response.put("accessToken", newAccessToken);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("토큰이 재발급 되었습니다", response));
    }

    // 키를 교환
    @GetMapping("/oauth2/success")
    public ResponseEntity<ApiResponseDTO> oauth2Success(@RequestParam("key") String key){
        Map<String, String> tokens = redisTemplate.opsForHash().entries(key);
        if(tokens == null || tokens.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDTO.of("유효 시간 만료", null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("로그인 성공", tokens));
    }

    // 임시 토큰 발급
    @PostMapping("/tmp-token")
    public ResponseEntity<ApiResponseDTO> getTempToken(@RequestBody User user) {
        // 전화번호 값이 들어온다면 해당 전화번호를 기준으로 아이디를 조회 후 엑세스 토큰만 발급 (중복되는 전화번호는 추후 생각)

        Map<String, String> tokens = authService.issueTempAccessTokenByPhone(user);
        if (tokens == null || tokens.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.of("해당 전화번호로 사용자를 찾을 수 없습니다.", null));
        }
        return ResponseEntity.ok(ApiResponseDTO.of("임시 토큰 발급 완료", tokens));
    }

    // 문자로 인증코드 전송
    @PostMapping("/codes/sms")
    public ResponseEntity<ApiResponseDTO> sendAuthentificationCodeBySms(String phoneNumber, HttpSession session) {
        ApiResponseDTO response = smsService.sendAuthentificationCodeBySms(phoneNumber, session);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 이메일로 인증코드 전송
    @PostMapping("/codes/email")
    public ResponseEntity<ApiResponseDTO> sendAuthentificationCodeByEmail(String toEmail, HttpSession session) {
        ApiResponseDTO response = smsService.sendAuthentificationCodeByEmail(toEmail, session);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 인증코드 확인
    @PostMapping("/codes/verify")
    public ResponseEntity<ApiResponseDTO> verifyAuthentificationCode(String userAuthentificationCode, HttpSession session) {
        Map<String, Boolean> verified = new HashMap();
        String authentificationCode = (String)session.getAttribute("authentificationCode");
        verified.put("verified", authentificationCode.equals(userAuthentificationCode));
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("인증코드 확인 완료", verified));
    }

    @GetMapping("/exists/cehck-email")
    public ResponseEntity<ApiResponseDTO> existsCheckEmail(String email) {
        boolean isExistEmail = userService.existsByUserEmail(email);
        if(isExistEmail){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDTO.of("이미 존재하는 이메일", false));
        }
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("사용 가능한 이메일", true));
    }
}

