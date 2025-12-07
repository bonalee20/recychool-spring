package com.app.recychool.service;

import com.app.recychool.domain.entity.User;
import com.app.recychool.domain.entity.UserInsertSocial;
import com.app.recychool.domain.entity.UserSocial;
import com.app.recychool.domain.vo.UserSocialVO;
import com.app.recychool.domain.dto.UserResponseDTO;

import java.util.List;
import java.util.Map;

public interface UserService {

  // 회원 아이디 조회
  public Long getUserIdByUserEmail(String userEmail);

  // 회원 이메일 조회 : (전화번호로)
  public List<String> getUserEmailsByNameAndPhone(User user);

  // 회원 정보 조회
  public UserResponseDTO getUserById(Long id);

  // 마이페이지 정보 조회
//  public Map<String, Object> getMyDatas(Long id);

  // 이메일 중복 확인
  public boolean existsByUserEmail(String userEmail);

  // 회원 가입 후 로그인을 처리할 수 있도록
  public Map<String, String> register(User user);

  // 회원 가입(소셜 로그인)
  public Map<String, String> registerSocial(UserInsertSocial userInsertSocial, UserSocial userSocial);

  // 회원 정보 수정
  public UserResponseDTO modify(User user);
  // 회원 탈퇴
  public void withdraw(Long id);

}
