package com.app.recychool.domain.dto;

import com.app.recychool.domain.enums.VerificationStatus;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class IdentityStartResponseDTO {
    private Long verificationId;
    private String receiptId;
    private VerificationStatus status;
    private LocalDateTime expiresAt;
    private String tokenPlain; // ✅ 테스트/Mock에서만 사용 (운영에서는 내려주지 말 것)
}
