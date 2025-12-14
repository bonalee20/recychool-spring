package com.app.recychool.service;


import com.app.recychool.domain.dto.IdentityCompleteResponseDTO;
import com.app.recychool.domain.dto.IdentityStartRequestDTO;
import com.app.recychool.domain.dto.IdentityStartResponseDTO;

public interface IdentityVerificationService {
    public IdentityStartResponseDTO start(IdentityStartRequestDTO req);

    public IdentityCompleteResponseDTO mockComplete(Long verificationId, String tokenPlain);
    public void markDeviceTrusted(Long userId, String deviceId, String refreshTokenPlainOrNull);
}
