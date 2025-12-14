package com.app.recychool.domain.dto;

import com.app.recychool.domain.enums.IdentityProvider;
import com.app.recychool.domain.enums.VerificationPurpose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdentityStartRequestDTO {
    private IdentityProvider provider;          // KAKAO_CERT / NAVER_CERT / PASS_CERT
    private VerificationPurpose purpose;        // SIGN_UP / MY_PAGE
    private String userName;                        // 입력(포폴용)
    private String userPhone;                       // 입력(숫자만 권장)
    private String userBirthday;
}
