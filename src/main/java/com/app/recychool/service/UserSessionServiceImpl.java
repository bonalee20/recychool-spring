package com.app.recychool.service;

import com.app.recychool.domain.entity.UserSession;
import com.app.recychool.domain.enums.SessionStatus;
import com.app.recychool.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;

    @Override
    public void saveOrUpdateSession(Long userId, String deviceId, String refreshToken) {
        // 기존 세션 찾기 또는 새로 생성
        UserSession session = userSessionRepository.findTopByUserIdAndDeviceIdOrderByIdDesc(userId, deviceId)
                .orElseGet(() -> UserSession.builder()
                        .userId(userId)
                        .deviceId(deviceId)
                        .status(SessionStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .build());

        // RefreshToken 해시 생성
        String refreshTokenHash = hashRefreshToken(refreshToken);

        // 세션 정보 업데이트
        session.setRefreshTokenHash(refreshTokenHash);
        session.setLastSeenAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusDays(7)); // 7일 후 만료
        session.setStatus(SessionStatus.ACTIVE);

        userSessionRepository.save(session);
    }

    private String hashRefreshToken(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }
}

