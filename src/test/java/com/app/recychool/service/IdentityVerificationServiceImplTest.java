package com.app.recychool.service;

import com.app.recychool.domain.dto.IdentityCompleteResponseDTO;
import com.app.recychool.domain.dto.IdentityStartRequestDTO;
import com.app.recychool.domain.dto.IdentityStartResponseDTO;
import com.app.recychool.domain.entity.User;
import com.app.recychool.domain.enums.IdentityProvider;
import com.app.recychool.domain.enums.VerificationPurpose;
import com.app.recychool.domain.enums.VerificationStatus;
import com.app.recychool.repository.UserRepository;
import com.app.recychool.util.IdentityCrypto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class IdentityVerificationServiceImplTest {

    @Autowired
    private IdentityVerificationService identityVerificationService;
    @Autowired
    private IdentityCrypto identityCrypto;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void startTest(){
        IdentityStartRequestDTO identityStartRequestDTO = new IdentityStartRequestDTO();
        identityStartRequestDTO.setUserBirthday("2000-02-08");
        identityStartRequestDTO.setUserPhone("010-3313-9339");
        identityStartRequestDTO.setUserName("이승찬");
        identityStartRequestDTO.setProvider(IdentityProvider.NAVER_CERT);
        identityStartRequestDTO.setPurpose(VerificationPurpose.SIGN_UP);
        IdentityStartResponseDTO start = identityVerificationService.start(identityStartRequestDTO);
        log.info("start 한 결과값{}", start);
    }

    @Test
    public void verifyTest(){
        // 1) start 요청 만들기
        IdentityStartRequestDTO req = new IdentityStartRequestDTO();
        req.setUserBirthday("2000-02-08");         // 너 서비스에서 birth 파싱/정규화 방식에 맞춰서
        req.setUserPhone("010-3313-9339");
        req.setUserName("이승찬");
        req.setProvider(IdentityProvider.NAVER_CERT);
        req.setPurpose(VerificationPurpose.SIGN_UP);

        // 2) start 호출 → verificationId, tokenPlain 확보
        IdentityStartResponseDTO startRes = identityVerificationService.start(req);
        log.info("start 결과: {}", startRes);

        Long verificationId = startRes.getVerificationId();
        String tokenPlain = startRes.getTokenPlain(); // ✅ 위에서 DTO에 추가한 필드

        // 안전 체크
        if (verificationId == null) throw new IllegalStateException("verificationId is null");
        if (tokenPlain == null || tokenPlain.isBlank()) throw new IllegalStateException("tokenPlain is empty");

        // 3) mockComplete 호출(= 토큰 해시 비교 → VERIFIED 처리)
        IdentityCompleteResponseDTO completeRes =
                identityVerificationService.mockComplete(verificationId, tokenPlain);

        log.info("complete 결과: {}", completeRes);

        org.junit.jupiter.api.Assertions.assertEquals(VerificationStatus.VERIFIED, completeRes.getStatus());
    }

    @Test
    public void startAndVerify_existingUser_true_Test() {

        // ✅ start에서 넣을 입력값(= identityKey 생성 재료)
        String name = "이승찬";
        String phone = "010-3313-9339";
        String birth = "2000-02-08";

        // 1) identityKey 미리 생성
        String identityKey = identityCrypto.identityKey(name, phone, birth);

        // 2) 기존 유저를 DB에 먼저 넣기 (identityKey 심기)
        User user = User.builder()
                .userEmail("exist@test.com")        // 아무 값 OK (unique 제약 있으면 유니크로)
                .userName(name)
                .userPhone(phone)
                .userBirthday(new java.util.Date()) // 너 엔티티가 Date라서 임시
                .userProvider("local")
                .userIdentityKey(identityKey)       // ✅ 핵심
                .userIdentityProvider(IdentityProvider.NAVER_CERT) // 선택
                .userIdentityVerifiedAt(LocalDateTime.now())       // 선택
                .build();

        userRepository.save(user);

        // 3) 이제 start 실행하면 동일 identityKey가 생성됨
        IdentityStartRequestDTO req = new IdentityStartRequestDTO();
        req.setUserName(name);
        req.setUserPhone(phone);
        req.setUserBirthday(birth);
        req.setProvider(IdentityProvider.NAVER_CERT);
        req.setPurpose(VerificationPurpose.SIGN_UP);

        IdentityStartResponseDTO startRes = identityVerificationService.start(req);
        log.info("start 결과: {}", startRes);

        // 4) complete 실행 → existingUser가 true여야 함
        IdentityCompleteResponseDTO completeRes =
                identityVerificationService.mockComplete(startRes.getVerificationId(), startRes.getTokenPlain());

        log.info("complete 결과: {}", completeRes);

        org.junit.jupiter.api.Assertions.assertEquals(VerificationStatus.VERIFIED, completeRes.getStatus());
        org.junit.jupiter.api.Assertions.assertTrue(completeRes.isExistingUser());
    }
}