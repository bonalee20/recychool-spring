package com.app.recychool.domain.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TokenDTO {
    private Long userId;
    private String accessToken;
    private String refreshToken;
}
