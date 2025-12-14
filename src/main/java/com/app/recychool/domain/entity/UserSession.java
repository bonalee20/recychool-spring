package com.app.recychool.domain.entity;

import com.app.recychool.domain.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_USER_SESSION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(name="SEQ_USER_SESSION_GENERATOR", sequenceName="SEQ_USER_SESSION", allocationSize=1)
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_USER_SESSION_GENERATOR")
    private Long id;

    private Long userId;          // FK처럼 쓰되, 지금은 단순 Long로 OK
    private String deviceId;      // 쿠키 UUID
    private String refreshTokenHash; // 평문 저장 X

    @Enumerated(EnumType.STRING)
    private SessionStatus status; // ACTIVE, REVOKED

    private Boolean trusted;      // 본인인증 통과한 기기면 true

    private LocalDateTime createdAt;
    private LocalDateTime lastSeenAt;
    private LocalDateTime expiresAt;

    private String test;
}



