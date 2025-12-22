package com.app.recychool.service;

import com.app.recychool.domain.dto.*;
import com.app.recychool.domain.entity.User;
import com.app.recychool.domain.entity.UserInsertSocial;
import com.app.recychool.domain.entity.UserSocial;
import com.app.recychool.exception.UserException;
import com.app.recychool.repository.*;
import com.app.recychool.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenUtil jwtTokenUtil;
  private final UserSocialRepository userSocialRepository;

    // 이메일 중복 조회
  @Override
  public boolean existsByUserEmail(String userEmail) {
    return userRepository.existsByUserEmail(userEmail);
  }

  // 회원가입
  @Override
  public Map<String, String> register(User user) {

    // 1. 이메일 중복검사
    if(userRepository.existsByUserEmail(user.getUserEmail())) {
      throw new UserException("이미 존재하는 회원입니다");
    }

    // 2. 비밀번호 암호화
    // userVO.setUserPassword(passwordEncoder.encode(userVO.getUserPassword()));

    // 3. 회원 가입
    userRepository.save(user);
    return Map.of();
  }

  // 회원가입 소셜 (비밀번호가 없다)
  @Override
  public Map<String, String> registerSocial(
          UserInsertSocial userInsertSocial,
      UserSocial userSocial
  ) {

    Map<String, String> claim = new HashMap<>();
    Map<String, String> tokens = new HashMap<>();

    if (userInsertSocial.getUserEmail() == null || userInsertSocial.getUserEmail().isBlank()) {
      throw new UserException("소셜 이메일 동의가 필요합니다.");
    }
    if(userRepository.existsByUserEmail(userInsertSocial.getUserEmail())) {
      throw new UserException("이미 존재하는 회원입니다.");
    }

    // 회원 가입
    User newUser = new User(userInsertSocial);
    if (newUser.getUserName() == null || newUser.getUserName().isBlank()) {
      String fallback = (newUser.getUserProvider() != null ? newUser.getUserProvider() : "SOCIAL") + "_USER_" +
          java.util.UUID.randomUUID().toString().substring(0,6);
      newUser.setUserName(fallback);
    }

    userRepository.save(newUser);

    // 가입한 회원 정보
    String userEmail = userInsertSocial.getUserEmail();

    // 가입한 회원의 ID (저장된 User 엔티티 다시 조회)
    User savedUser = userRepository.findIdByUserEmail(userEmail);
    Long userId = savedUser.getId();
    claim.put("userEmail", userEmail);
    String refreshToken = jwtTokenUtil.generateRefreshToken(claim);
    String accessToken = jwtTokenUtil.generateAccessToken(claim);

    // 소셜 테이블에 추가 (새 엔티티로 저장)
    userSocial.setId(null); // ID 초기화 (새로 생성)
    userSocial.setUser(savedUser);
    userSocialRepository.save(userSocial);

    // 토큰을 담아서 반환
    tokens.put("accessToken", accessToken);
    tokens.put("refreshToken", refreshToken);

    return tokens;
  }

  // 회원 이메일로 아이디 조회
  @Override
  public Long getUserIdByUserEmail(String userEmail) {
    return userRepository.findIdByUserEmail(userEmail).getId();
  }

  @Override
  public List<String> getUserEmailsByNameAndPhone(User user){
    List<String> userEmails = userRepository.findUserEmailByUserNameAndUserPhone(user.getUserName(), user.getUserPhone());
    return userEmails;
  }

  // 회원 조회
  @Override
  public UserResponseDTO getUserById(Long id) {
    return userRepository.findById(id).map(UserResponseDTO::new).orElseThrow(() -> new UserException("회원 조회 실패"));
  }

  // 회원 정보 수정
  @Override
  public UserResponseDTO modify(User user) {
    if(user.getUserPassword() != null && !user.getUserPassword().isBlank()) {
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
    }
    userRepository.save(user);
    // 수정 후 업데이트된 사용자 정보 반환
    return getUserById(user.getId());
  }

    // 회원 탈퇴
  @Override
  public void withdraw(Long id) {
    userSocialRepository.deleteById(id);
    userRepository.deleteById(id);
  }

//  로그아웃 시 현재 로그인 상태 변경 서비스
    @Override
    public void modifyUserIsLogin(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("유저를 찾을 수 없습니다."));
        user.setUserIsLogin(0);
    }

    @Override
    public boolean findUserByUserNameAndUserPhone(String userName, String userPhone) {
//      이름 + 전화번호로 조회된 회원이 없다 == 회원가입한 적 없음 => false 리턴
        if(userRepository.findUserEmailByUserNameAndUserPhone(userName, userPhone).isEmpty()){
            return false;
        }
        return true;
    }

    @Override
    @Transactional // 트랜잭션 종료 시 변경 감지(Dirty Checking)가 동작합니다.
    public void updateUserInfo(Long userId, UserUpdateDTO updateDTO) {

        // 1. 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        // 2. 값 변경 (Setter 직접 사용)
        // [추가됨] 이름 수정 로직
        if (updateDTO.getUserName() != null && !updateDTO.getUserName().isBlank()) {
            user.setUserName(updateDTO.getUserName());
        }
        // 전화번호 수정 로직 (값이 있을 때만 set 호출)
        if (updateDTO.getUserPhone() != null && !updateDTO.getUserPhone().isBlank()) {
            user.setUserPhone(updateDTO.getUserPhone());
        }

        // 비밀번호 수정 로직 (값이 있을 때만 암호화 후 set 호출)
        if (updateDTO.getUserPassword() != null && !updateDTO.getUserPassword().isBlank()) {
            String encryptedPassword = passwordEncoder.encode(updateDTO.getUserPassword());
            user.setUserPassword(encryptedPassword);
        }
    }
}
