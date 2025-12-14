package com.app.recychool.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    // 로그인 즉시 성공
    private String accessToken;
    private String refreshToken;

    // Step-up 필요
    private boolean stepUpRequired;
    private Long verificationId;
    private String receiptId;
    private LocalDateTime expiresAt;
}

