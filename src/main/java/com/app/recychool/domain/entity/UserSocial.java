package com.app.recychool.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_USER_SOCIAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "SEQ_USER_SOCIAL_GENERATOR",
        sequenceName = "SEQ_USER_SOCIAL",
        allocationSize = 1
)
public class UserSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_USER_SOCIAL_GENERATOR")
    private Long id;

    private String userSocialProviderId;
    private String userSocialProvider;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

}
