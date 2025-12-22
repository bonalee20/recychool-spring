package com.app.recychool.domain.entity;

import com.app.recychool.domain.dto.UserResponseDTO;
import com.app.recychool.domain.enums.IdentityProvider;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "TBL_USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "SEQ_USER_GENERATOR",
        sequenceName = "SEQ_USER",
        allocationSize = 1
)
@ToString(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_USER_GENERATOR")
    private Long id;
    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate userBirthday;
    private String userEmail;
    private String userPhone;
    private String userPassword;
    private String userProvider;

    @Column(name = "USER_IS_LOGIN", nullable = true)
    private Integer userIsLogin;

    private String userIdentityKey;
    
    @Enumerated(EnumType.STRING)
    private IdentityProvider userIdentityProvider;
    
    private LocalDateTime userIdentityVerifiedAt;

    @JoinColumn(name = "USER_SOCIAL_ID")
    @OneToOne
    private UserSocial userSocial;

    public User(UserInsertSocial userInsertSocial) {
        this.id = userInsertSocial.getId();
        this.userEmail = userInsertSocial.getUserEmail();
        this.userName = userInsertSocial.getUserName() == null ? userInsertSocial.getUserNickname() : userInsertSocial.getUserName();
        this.userProvider = userInsertSocial.getUserProvider();
        this.userBirthday = userInsertSocial.getUserBirthday();
        this.userPhone = userInsertSocial.getUserPhone();
        if (this.userProvider == null) this.userProvider = "local";
    }

    public User(UserResponseDTO userResponseDTO) {
        this.id = userResponseDTO.getId();
        this.userEmail = userResponseDTO.getUserEmail();
        this.userName = userResponseDTO.getUserName();
        this.userProvider = userResponseDTO.getUserProvider();
        this.userBirthday = userResponseDTO.getUserBirthday();
        this.userPhone = userResponseDTO.getUserPhone();
        if (this.userProvider == null) this.userProvider = "local";
    }

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<MovieReservation> movieReservations;


}
