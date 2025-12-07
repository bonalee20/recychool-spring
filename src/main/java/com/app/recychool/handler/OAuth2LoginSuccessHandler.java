package com.app.recychool.handler;

import com.app.recychool.domain.dto.TokenDTO;
import com.app.recychool.domain.dto.UserResponseDTO;
import com.app.recychool.domain.entity.UserInsertSocial;
import com.app.recychool.domain.entity.UserSocial;
import com.app.recychool.service.AuthService;
import com.app.recychool.service.UserService;
import com.app.recychool.service.UserSocialService;
import com.app.recychool.service.UserSocialServiceImpl;
import com.app.recychool.util.JwtTokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final UserService userService;
  private final UserSocialServiceImpl userSocialService;
  private final JwtTokenUtil jwtTokenUtil;
  private final RedisTemplate redisTemplate;
  private final AuthService authService;

  // 소셜로그인 인가된 데이터가 들어온다.
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {
    try {
      log.info("OAuth2 로그인 성공 핸들러 시작");
      log.info("Authentication 타입: {}", authentication.getClass().getName());
      if (authentication instanceof OAuth2AuthenticationToken authToken) {
        log.info("OAuth2AuthenticationToken 확인됨");
      OAuth2User user = authToken.getPrincipal();
      Map<String, Object> attributes = user.getAttributes();

      // naver, google, kakao
      String userProvider = authToken.getAuthorizedClientRegistrationId();
      log.info("Provider: {}", userProvider);
      String userEmail = null;
      String userName = null;
      String userSocialProviderId = null;
      Long userId = null;
      Map<String, String> tokens = null;

      // 1. 어디로 들어왔는지를 확인
      if ("google".equals(userProvider)) {
        userEmail = (String) attributes.get("email");
        userName = (String) attributes.get("name");
        userSocialProviderId = (String) attributes.get("sub");
      } else if ("naver".equals(userProvider)) {
        Map<String, Object> resp = (Map<String, Object>) attributes.get("response");
        if (resp != null) {
          userEmail = (String) resp.get("email");
          userName = (String) resp.get("name");
          userSocialProviderId = (String) resp.get("id");
        }
      } else if ("kakao".equals(userProvider)) {
        Object idObj = attributes.get("id");
        userSocialProviderId = (idObj != null) ? String.valueOf(idObj) : null;

        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        if (account != null) {
          Boolean hasEmail = (Boolean) account.get("has_email");
          Boolean needsAgree = (Boolean) account.get("email_needs_agreement"); // 일부 SDK는 account_email_needs_agreement
          if (Boolean.TRUE.equals(hasEmail) && !Boolean.TRUE.equals(needsAgree)) {
            userEmail = (String) account.get("email");
          }
          Map<String, Object> profile = (Map<String, Object>) account.get("profile");
          if (profile != null) {
            userName = (String) profile.get("nickname");
          }
        }
      }

      // ★ provider 표준화(대문자) — 저장/조회/비교 일관성
      String normalizedProvider = (userProvider == null) ? null : userProvider.toUpperCase();

      // 2-A) 이메일이 없는 경우: 이미 링크된 소셜키(provider + providerId)로 재로그인 시도
      if (userEmail == null || userEmail.isBlank()) {
        if (normalizedProvider != null && userSocialProviderId != null && !userSocialProviderId.isBlank()) {
          Long linkedUserId = userSocialService.findUserIdByProvider(normalizedProvider, userSocialProviderId);
          if (linkedUserId != null) {
            // 기존 회원 → 이메일 조회 후 바로 토큰 발급
            UserResponseDTO foundUser = userService.getUserById(linkedUserId);

            Map<String, String> claim = new HashMap<>();
            claim.put("userEmail", foundUser.getUserEmail());
            Map<String, String> tmpTokens = new HashMap<>();
            String accessToken = jwtTokenUtil.generateAccessToken(claim);
            String refreshToken = jwtTokenUtil.generateRefreshToken(claim);
            tmpTokens.put("accessToken", accessToken);
            tmpTokens.put("refreshToken", refreshToken);

            // 교환키/redis
            String key = UUID.randomUUID().toString();
            redisTemplate.opsForHash().putAll(key, tmpTokens);
            redisTemplate.expire(key, 5, TimeUnit.MINUTES);

            // refresh 보관
            TokenDTO tokenDTO = new TokenDTO();
            tokenDTO.setUserId(foundUser.getId());
            tokenDTO.setRefreshToken(refreshToken);
            authService.saveRefreshToken(tokenDTO);

            // 쿠키
            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true).path("/").maxAge(60L * 60 * 24 * 7).build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // 성공 리다이렉트 후 즉시 종료
            getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000?key=" + key);
            return;
          }
        }

        // 링크도 없으면 이메일 동의 안내 페이지로
        getRedirectStrategy().sendRedirect(
            request, response, "http://localhost:3000/oauth2/require-email?provider=" + userProvider
        );
        return;
      }

      // 2. 이미 회원가입이 되어있는지
      if (userService.existsByUserEmail(userEmail)) {
        userId = userService.getUserIdByUserEmail(userEmail);
        UserResponseDTO foundUser = userService.getUserById(userId);

//        // 3. 어디로 접속했는지 확인!
//        log.info("이미 회원가입 됨: {}", foundUser);
//        log.info("이미 회원가입 된 아이디: {}", userId);

        // 4. 이미 회원가입이라면 토큰 발급
        List<String> providers = userSocialService.findAllProvidersById(userId);
        log.info("조회된 providers: {}, 현재 로그인 provider: {}", providers, userProvider);

        boolean isProviderConfirm = false;
        for (String provider : providers) {
          log.info("비교 중 - 저장된 provider: {}, 현재 provider: {}", provider, userProvider);
          if (provider != null && provider.equalsIgnoreCase(userProvider)) {
            isProviderConfirm = true;
            log.info("Provider 일치 확인됨!");
            break;
          }
        }

        log.info("isProviderConfirm: {}", isProviderConfirm);

        if (isProviderConfirm) {
          log.info("Provider 일치 - 토큰 발급 및 메인페이지 이동");
          // 아이디 같고, 프로바이더도 일치
          Map<String, String> claim = new HashMap<>();
          claim.put("userEmail", foundUser.getUserEmail());
          String accessToken = jwtTokenUtil.generateAccessToken(claim);
          String refreshToken = jwtTokenUtil.generateRefreshToken(claim);

          tokens = new HashMap<>();
          tokens.put("accessToken", accessToken);
          tokens.put("refreshToken", refreshToken);

        } else {
          // 아이디는 같지만 프로바이더 불일치
          // 계정 통합 - 화면으로 리다이렉트 시킨 후 인증절차 필요
          log.warn("Provider 불일치 - 계정 통합 필요. 저장된 providers: {}, 현재 provider: {}", providers, userProvider);
          String redirectUrl = "http://localhost:3000/oauth2/confirm?provider=" + userProvider;
          getRedirectStrategy().sendRedirect(request, response, redirectUrl);
          return;
        }

      } else {
        // 4. 신규 회원가입 후 토큰 발급
        UserInsertSocial newUser = new UserInsertSocial();
        newUser.setUserEmail(userEmail);
        newUser.setUserName(userName);
        newUser.setUserProvider(userProvider);

        UserSocial userSocial = new UserSocial();
        userSocial.setUserSocialProvider(userProvider);
        userSocial.setUserSocialProviderId(userSocialProviderId);

        tokens = userService.registerSocial(newUser, userSocial);
        userId = userService.getUserIdByUserEmail(userEmail);
        if (userId == null) {
          // 비정상: 안전 리다이렉트
          getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/sign-in");
          return;
        }
      }

      String refreshToken = tokens.get("refreshToken");
      tokens.remove("refreshToken");

      // 5. redis로 교환하기 위한 key를 등록
      String key = UUID.randomUUID().toString();
      redisTemplate.opsForHash().putAll(key, tokens);
      redisTemplate.expire(key, 5, TimeUnit.MINUTES);

      // 6. redis에 refresh 토큰을 등록 (검증)
      TokenDTO tokenDTO = new TokenDTO();
      tokenDTO.setUserId(userId);
      tokenDTO.setRefreshToken(refreshToken);
      authService.saveRefreshToken(tokenDTO);

      // 7. 쿠키에 심는다
      ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
          .httpOnly(true) // *필수
          // .secure(true) // https에서 사용
          .path("/")      // 모든 경로에 쿠키 전송 사용
          .maxAge(60 * 60 * 24 * 7)
          .build();

      response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

      // 7. 프론트 React 리다이렉트(3000번포트로) - 메인페이지로 이동
      String redirectUrl = "http://localhost:3000?key=" + key;
      getRedirectStrategy().sendRedirect(request, response, redirectUrl);
      } else {
        // OAuth2AuthenticationToken이 아닌 경우
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/sign-in");
      }
    } catch (Exception e) {
      // 예외 발생 시 실패 페이지로 리다이렉트
      log.error("OAuth2 로그인 처리 중 예외 발생", e);
      e.printStackTrace();
      getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/sign-in");
    }
  }
}
