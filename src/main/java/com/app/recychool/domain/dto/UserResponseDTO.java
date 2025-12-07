package com.app.recychool.domain.dto;

import com.app.recychool.domain.entity.User;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String userName;
    private Date userBirthday;
    private String userEmail;
    private String userPhone;
    private String userPassword;
    private String userProvider;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.userEmail = user.getUserEmail();
        this.userName = user.getUserName();
        this.userProvider = user.getUserProvider();
        this.userBirthday = user.getUserBirthday();
        this.userPhone = user.getUserPhone();
        this.userPassword = user.getUserPassword();
    }
}
