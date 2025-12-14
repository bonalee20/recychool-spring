package com.app.recychool.service;

public interface UserSessionService {
    void saveOrUpdateSession(Long userId, String deviceId, String refreshToken);
}

