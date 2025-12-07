package com.app.recychool.domain.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSocialVO {
    private Long id;
    private String userSocialProviderId;
    private String userSocialProvider;
    private Long userId;
}
