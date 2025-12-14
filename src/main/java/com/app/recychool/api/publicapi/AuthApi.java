package com.app.recychool.api.publicapi;


import com.app.recychool.domain.dto.*;
import com.app.recychool.domain.entity.User;
import com.app.recychool.domain.enums.SessionStatus;
import com.app.recychool.repository.UserRepository;
import com.app.recychool.repository.UserSessionRepository;
import com.app.recychool.service.AuthService;
import com.app.recychool.service.IdentityVerificationService;
import com.app.recychool.service.SmsService;
import com.app.recychool.service.UserService;
import com.app.recychool.service.UserSessionService;
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

import java.net.Authenticator;
import java.util.HashMap;
import java.util.Map;
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
    private final IdentityVerificationService identityVerificationService;
    private final SmsService smsService;
    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;
    private final DeviceIdResolver deviceIdResolver;
    private final UserSessionService userSessionService;
    // 로그인
    @PostMapping("login")
    public ResponseEntity<ApiResponseDTO> login(
            @RequestBody User user,
            HttpServletRequest request,
            HttpServletResponse response

    ){
        Long userId = userRepository.findIdByUserEmail(user.getUserEmail()).getId();

        if(userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseDTO.of("입력한 이메일을 찾을 수 없습니다."));
        }
        user.setId(userId);

        // DeviceIdResolver로 deviceId 가져오기 (없으면 생성하고 쿠키 설정)
        String deviceId = deviceIdResolver.resolveOrCreate(request, response);

        // 신뢰된 기기인지 확인
//        유저 아이디로 세션을 찾아서 세션에 저장되어 있고, 활성화된 세션일 경우에만 로그인 성공
        boolean trusted = userSessionRepository
                .existsByUserIdAndDeviceIdAndTrustedTrueAndStatus(user.getId(), deviceId, SessionStatus.ACTIVE);
        
        if (!trusted) {
            // 여기서 막고 본인인증으로 보낸다
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDTO.of("새 기기입니다. 본인인증 후 로그인하세요.",
                            Map.of("code", "DEVICE_NOT_TRUSTED",
                                    "userId", String.valueOf(user.getId()))));
        }else {
//            신뢰된 기기에서 로그인 했을 때 위치
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

            // 7. UserSession 저장/업데이트 (로그인 성공 시 세션 정보 저장)
            userSessionService.saveOrUpdateSession(user.getId(), deviceId, refreshToken);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(ApiResponseDTO.of("로그인이 성공했습니다", tokens));
        }



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

    // 인증코드 확인
    @PostMapping("/codes/verify")
    public ResponseEntity<ApiResponseDTO> verifyAuthentificationCode(String userAuthentificationCode, HttpSession session) {
        Map<String, Boolean> verified = new HashMap();
        String authentificationCode = (String)session.getAttribute("authentificationCode");
        verified.put("verified", authentificationCode.equals(userAuthentificationCode));
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("인증코드 확인 완료", verified));
    }


//    여기부터 본인인증 api
    @PostMapping("/identity/start")
    public ResponseEntity<ApiResponseDTO> startIdentityVerification(@RequestBody IdentityStartRequestDTO req) {
        IdentityStartResponseDTO start = identityVerificationService.start(req);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("인증 완료", start));
    }

    @PostMapping("/complete/{verificationId}")
    public ResponseEntity<IdentityCompleteResponseDTO> complete(
            @PathVariable Long verificationId,
            @RequestBody IdentityCompleteRequestDTO req
    ) {
        return ResponseEntity.ok(identityVerificationService.mockComplete(verificationId, req.getTokenPlain()));
    }

    // UserSession 저장/업데이트 API
    @PostMapping("/session/save")
    public ResponseEntity<ApiResponseDTO> saveUserSession(
            @RequestParam Long userId,
            @RequestParam String deviceId,
            @RequestParam String refreshToken
    ) {
        userSessionService.saveOrUpdateSession(userId, deviceId, refreshToken);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDTO.of("세션이 저장되었습니다", null));
    }

    // 기기 등록 API (본인인증 완료 후 호출)
    @PostMapping("/device/register")
    public ResponseEntity<ApiResponseDTO> registerDevice(
            @RequestParam Long userId,
            @RequestParam(required = false) String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // DeviceIdResolver로 deviceId 가져오기 (없으면 생성하고 쿠키 설정)
        String deviceId = deviceIdResolver.resolveOrCreate(request, response);

        // 기기 신뢰 처리 (trusted = true로 설정)
        identityVerificationService.markDeviceTrusted(userId, deviceId, refreshToken);

        // refreshToken이 있으면 UserSession에 저장
        if (refreshToken != null && !refreshToken.isBlank()) {
            userSessionService.saveOrUpdateSession(userId, deviceId, refreshToken);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDTO.of("기기가 등록되었습니다", 
                        Map.of("deviceId", deviceId, "userId", String.valueOf(userId))));
    }

}

