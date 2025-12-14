package com.app.recychool.domain.dto;

import com.app.recychool.domain.enums.VerificationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class IdentityCompleteResponseDTO {
    private Long verificationId;
    private VerificationStatus status;
    private boolean existingUser;   // 기존회원 여부
}