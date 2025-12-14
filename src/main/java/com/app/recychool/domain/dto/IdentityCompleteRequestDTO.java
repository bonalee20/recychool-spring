package com.app.recychool.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentityCompleteRequestDTO {
    private String tokenPlain;  // 사용자가(또는 mock) 들고 있던 원문 토큰
}
