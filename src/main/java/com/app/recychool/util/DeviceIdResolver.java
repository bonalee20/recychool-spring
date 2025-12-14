package com.app.recychool.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeviceIdResolver {

    public static final String COOKIE_NAME = "deviceId";

    /**
     * 요청에서 deviceId를 찾거나, 없으면 새로 생성하여 쿠키에 설정하고 반환합니다.
     * @param req HttpServletRequest
     * @param res HttpServletResponse
     * @return deviceId (기존 또는 새로 생성된 값)
     */
    public String resolveOrCreate(HttpServletRequest req, HttpServletResponse res) {
        String found = null;

        // 기존 쿠키에서 deviceId 찾기
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if (COOKIE_NAME.equals(c.getName())) {
                    found = c.getValue();
                    break;
                }
            }
        }

        // 기존 deviceId가 있으면 반환
        if (found != null && !found.isBlank()) {
            return found;
        }

        // 없으면 새로 생성
        String newId = UUID.randomUUID().toString();

        // ResponseCookie로 설정 (더 현대적인 방식)
        ResponseCookie deviceCookie = ResponseCookie.from(COOKIE_NAME, newId)
                .httpOnly(true)      // JS로 못 읽게(보안)
                .path("/")           // 전 경로에서 사용
                .maxAge(60L * 60 * 24 * 365) // 1년
                .build();

        // 브라우저에 deviceId 쿠키 심기
        res.addHeader(HttpHeaders.SET_COOKIE, deviceCookie.toString());

        return newId;
    }
}
