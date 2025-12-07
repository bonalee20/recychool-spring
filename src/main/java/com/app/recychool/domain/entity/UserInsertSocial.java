package com.app.recychool.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_USER_INSERT_SOCIAL")
@SequenceGenerator(name = "SEQ_USER_INSERT_SOCIAL_GENERATOR", sequenceName = "SEQ_USER_INSERT_SOCIAL")
@Builder
public class UserInsertSocial {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_USER_INSERT_SOCIAL")
    private Long id;
    private String userName;
    private Date userBirthday;
    private String userEmail;
    private String userPhone;
    private String userThumbnailName;
    private String userThumbnailUrl;
    private String userNickname;
    private String userProvider;
}
