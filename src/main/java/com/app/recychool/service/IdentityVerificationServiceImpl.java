package com.app.recychool.service;

import com.app.recychool.domain.dto.IdentityCompleteResponseDTO;
import com.app.recychool.domain.dto.IdentityStartRequestDTO;
import com.app.recychool.domain.dto.IdentityStartResponseDTO;
import com.app.recychool.domain.entity.IdentityVerification;
import com.app.recychool.domain.entity.UserSession;
import com.app.recychool.domain.enums.SessionStatus;
import com.app.recychool.domain.enums.VerificationStatus;
import com.app.recychool.exception.VerifiedException;
import com.app.recychool.repository.IdentityVerificationRepository;
import com.app.recychool.repository.UserRepository;
import com.app.recychool.repository.UserSessionRepository;
import com.app.recychool.util.IdentityCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class IdentityVerificationServiceImpl implements IdentityVerificationService {

    private final IdentityVerificationRepository identityVerificationRepository;
    private final IdentityCrypto identityCrypto;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;


    @Override
    public IdentityStartResponseDTO start(IdentityStartRequestDTO req) {
        String identityKey = identityCrypto.identityKey(req.getUserName(), req.getUserPhone(), req.getUserBirthday());

        // 2) requestToken 원문 생성(클라에 내려줄 값) + 해시 저장
        String tokenPlain = identityCrypto.newTokenPlain();
        String tokenHash = identityCrypto.tokenHash(tokenPlain);

        // 3) receiptId(Mock용) 생성
        String receiptId = "MOCK-" + UUID.randomUUID();

        // 4) 만료 시간 세팅
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(10);

        IdentityVerification verification = IdentityVerification.builder()
                .identityProvider(req.getProvider())
                .verificationPurpose(req.getPurpose())
                .verificationStatus(VerificationStatus.IN_PROGRESS)
                .receiptId(receiptId)
                .identityKey(identityKey)
                .tokenHash(tokenHash)
                .tokenIssuedAt(now)
                .tokenExpiresAt(expiresAt)
                .build();

        IdentityVerification saved = identityVerificationRepository.save(verification);

        return IdentityStartResponseDTO
                    .builder()
                    .verificationId(saved.getId())
                    .receiptId(saved.getReceiptId())
                    .status(saved.getVerificationStatus())
                    .expiresAt(saved.getTokenExpiresAt())
                    .tokenPlain(tokenPlain)
                    .build();

    }


    @Override
    public IdentityCompleteResponseDTO mockComplete(Long verificationId, String tokenPlain) {
        IdentityVerification v = identityVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new IllegalArgumentException("verification not found: " + verificationId));

        // 1) 만료 체크
        if (v.getTokenExpiresAt() != null && LocalDateTime.now().isAfter(v.getTokenExpiresAt())) {
            v.setVerificationStatus(VerificationStatus.EXPIRED);
            return IdentityCompleteResponseDTO.builder()
                    .verificationId(v.getId())
                    .status(v.getVerificationStatus())
                    .existingUser(false)
                    .build();
        }

        // 2) token 비교 (원문 저장 X → 들어온 tokenPlain을 해시해서 DB tokenHash와 비교)
        String incomingHash = identityCrypto.tokenHash(tokenPlain);

        if (!incomingHash.equals(v.getTokenHash())) {
            v.setVerificationStatus(VerificationStatus.FAILED);
            return IdentityCompleteResponseDTO.builder()
                    .verificationId(v.getId())
                    .status(v.getVerificationStatus())
                    .existingUser(false)
                    .build();
        }

        // 3) 성공 처리
        v.setVerificationStatus(VerificationStatus.VERIFIED);

        // 4) 기존회원 여부 체크 (핵심)
        boolean exists = userRepository.existsByUserIdentityKey(v.getIdentityKey());

        return IdentityCompleteResponseDTO.builder()
                .verificationId(v.getId())
                .status(v.getVerificationStatus())
                .existingUser(exists)
                .build();
    }
    @Override
    public void markDeviceTrusted(Long userId, String deviceId, String refreshTokenPlainOrNull) {
        UserSession session = userSessionRepository.findTopByUserIdAndDeviceIdOrderByIdDesc(userId, deviceId)
                .orElseGet(() -> UserSession.builder()
                        .userId(userId)
                        .deviceId(deviceId)
                        .status(SessionStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .build());

        session.setTrusted(true);
        session.setLastSeenAt(LocalDateTime.now());
        // refreshTokenHash는 로그인 성공 시점에 세팅해도 되고, 여기서 세팅해도 됨
        userSessionRepository.save(session);
    }

}
