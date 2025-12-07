package com.app.recychool.service;

import com.app.recychool.domain.dto.TokenDTO;
import com.app.recychool.domain.dto.UserResponseDTO;
import com.app.recychool.domain.entity.User;
import com.app.recychool.exception.JwtTokenException;
import com.app.recychool.exception.UserException;
import com.app.recychool.repository.UserRepository;
import com.app.recychool.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AuthServiceImpl implements AuthService {

  @Value("${jwt.token-blacklist-prefix}")
  private String BLACKLIST_TOKEN_PREFIX;

  @Value("${jwt.refresh-blacklist-prefix}")
  private String REFRESH_TOKEN_PREFIX;

  private final UserRepository userRepository;
  private final JwtTokenUtil jwtTokenUtil;
  private final PasswordEncoder passwordEncoder;
  private final RedisTemplate redisTemplate;

  @Override
  public Map<String, String> login(User user) {

    Map<String, String> claim = new HashMap<>();
    Map<String,String> tokens = new HashMap<>();

    // 1. 아이디 확인
    if(!userRepository.existsByUserEmail(user.getUserEmail())) {
      throw new UserException("아이디를 확인해주세요");
    }

    // 2. 비밀번호 확인
    Long userId = userRepository.findIdByUserEmail(user.getUserEmail()).getId();
    User foundUser = userRepository.findById(userId).orElseThrow(() -> new UserException("회원이 없습니다"));
//    인코딩 안 쓸 때 주석처리
    if(!passwordEncoder.matches(user.getUserPassword(), foundUser.getUserPassword())) {
      throw new UserException("비밀번호를 확인해주세요.");
    }

    // 3. 토큰 생성
    claim.put("userEmail", user.getUserEmail());
    String accessToken = jwtTokenUtil.generateAccessToken(claim);
    String refreshToken = jwtTokenUtil.generateRefreshToken(claim);

    // 4. 토큰을 Redis에 저장
    TokenDTO tokenDTO = new TokenDTO();
    tokenDTO.setUserId(foundUser.getId());
    tokenDTO.setRefreshToken(refreshToken);
    tokenDTO.setAccessToken(accessToken);
    saveRefreshToken(tokenDTO);

    // 5. 클라이언트에 토큰 반환
    tokens.put("accessToken", accessToken);
    tokens.put("refreshToken", refreshToken);
    return tokens;
  }

  @Override
  public Map<String, String> issueTempAccessTokenByPhone(User user){

    Map<String, String> claim = new HashMap<>();
    Map<String,String> tokens = new HashMap<>();

    Long userId = userRepository.findIdByUserEmailAndUserPhone(user.getUserEmail(), user.getUserPhone()).getId();
    User foundUser = userRepository.findById(userId).orElseThrow(() -> new UserException("회원이 없습니다"));

    claim.put("userEmail", foundUser.getUserEmail());
    String accessToken = jwtTokenUtil.generateAccessToken(claim);

    tokens.put("accessToken", accessToken);

    return tokens;
  }

  @Override
  public boolean saveRefreshToken(TokenDTO tokenDTO) {
    Long id = tokenDTO.getUserId();
    String refreshToken = tokenDTO.getRefreshToken();
    try {
      String key = REFRESH_TOKEN_PREFIX + id;
      redisTemplate.opsForValue().set(key, refreshToken, 7, TimeUnit.DAYS);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean validateRefreshToken(TokenDTO tokenDTO) {
    Long id = tokenDTO.getUserId();
    String refreshToken = tokenDTO.getRefreshToken();

    String key = REFRESH_TOKEN_PREFIX + id;
    try {
      String storedToken = redisTemplate.opsForValue().get(key).toString();
      if(!refreshToken.equals(storedToken)) {
        return false;
      }

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public String reissueAccessToken(TokenDTO tokenDTO) {
    Map<String,String> claim = new HashMap<>();

    // 토큰에서 email을 가져온다.
    String userEmail = (String) jwtTokenUtil.getUserEmailFromToken(tokenDTO.getRefreshToken()).get("userEmail");
    Long id =  userRepository.findIdByUserEmail(userEmail).getId();
    tokenDTO.setUserId(id);

    // 1. 기존 RefreshToken 또는 AccessToken 블랙리스트인지 확인
    if(isBlackedRefreshToken(tokenDTO)) {
      throw new JwtTokenException("이미 로그아웃된 토큰입니다. 다시 로그인하세요");
    }

    // 2. 리프레쉬 토큰 검증
    if(!validateRefreshToken(tokenDTO)) {
      throw new JwtTokenException("Refresh Token이 유효하지 않습니다. 다시 로그인하세요");
    }

    // 3. User 정보 조회
      User user = userRepository.findById(id).orElseThrow(() -> new UserException("회원 정보를 찾을 수 없습니다"));
    claim.put("userEmail", user.getUserEmail());
    String newAccessToken = jwtTokenUtil.generateAccessToken(claim);
    return newAccessToken;
  }

  @Override
  public boolean revokeRefreshToken(TokenDTO tokenDTO) {
    Long id = tokenDTO.getUserId();
    String refreshToken = tokenDTO.getRefreshToken();
    String key = REFRESH_TOKEN_PREFIX + id;

    try {
      Object storedTokenObj = redisTemplate.opsForValue().get(key);
      if(storedTokenObj != null) {
        String storedToken = storedTokenObj.toString();
        // 저장된 토큰과 일치하면 삭제
        if(storedToken.equals(refreshToken)) {
          redisTemplate.delete(key);
          return true;
        }
      }
      // 토큰이 없거나 일치하지 않아도 true 반환 (로그아웃은 완료)
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  // 탈취 방어
  @Override
  public boolean saveBlacklistedToken(TokenDTO tokenDTO) {

    // 토큰에서 email을 가져온다.
    String userEmail = (String) jwtTokenUtil.getUserEmailFromToken(tokenDTO.getRefreshToken()).get("userEmail");
    Long id =  userRepository.findIdByUserEmail(userEmail).getId();
    String refreshToken = tokenDTO.getRefreshToken();
    String key = BLACKLIST_TOKEN_PREFIX + id;

    try {
      redisTemplate.opsForSet().add(key, refreshToken);
//        TTL 설정
      redisTemplate.expire(key, 7, TimeUnit.DAYS);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean isBlackedRefreshToken(TokenDTO tokenDTO) {
    Long id = tokenDTO.getUserId();
    String refreshToken = tokenDTO.getRefreshToken();
    String key = BLACKLIST_TOKEN_PREFIX + id;

    try {
      Boolean isUser = redisTemplate.opsForSet().isMember(key, refreshToken);
      return isUser != null && isUser;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public String getUserEmailFromAuthentication(Authentication authentication){
    Object p = authentication.getPrincipal();
    String result = null;
    if (p instanceof UserResponseDTO urdto) {
      result = urdto.getUserEmail();
    }
    if (authentication instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken oat) {
      var attrs = ((org.springframework.security.oauth2.core.user.OAuth2User) oat.getPrincipal()).getAttributes();
      String reg = oat.getAuthorizedClientRegistrationId();
      if ("google".equals(reg)) result =  (String) attrs.get("email");
      if ("naver".equals(reg))  result =  (String) ((Map<?,?>) attrs.get("response")).get("email");
      if ("kakao".equals(reg))  result =  (String) ((Map<?,?>) attrs.get("kakao_account")).get("email");
    }

    return result;
  }
}
