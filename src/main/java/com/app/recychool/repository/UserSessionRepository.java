package com.app.recychool.repository;

import com.app.recychool.domain.entity.UserSession;
import com.app.recychool.domain.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionRepository  extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByUserIdAndDeviceId(Long userId, String deviceId);
    Optional<UserSession> findByRefreshTokenHashAndStatus(String refreshTokenHash, SessionStatus status);
    boolean existsByUserIdAndDeviceIdAndTrustedTrueAndStatus(Long userId, String deviceId, SessionStatus status);
    Optional<UserSession> findTopByUserIdAndDeviceIdOrderByIdDesc(Long userId, String deviceId);
}