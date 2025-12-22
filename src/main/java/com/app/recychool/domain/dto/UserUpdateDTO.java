package com.app.recychool.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {
    private String userName;
    private String userPhone;
    private String userPassword;
}