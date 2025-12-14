package com.app.recychool.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_USER_DEVICE_ALLOW",
        uniqueConstraints = @UniqueConstraint(columnNames = {"USER_ID", "DEVICE_ID_HASH"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeviceAllow {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USER_DEVICE_ALLOW_GENERATOR")
    private Long id;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "DEVICE_ID_HASH", nullable = false, length = 128)
    private String deviceIdHash;

    @Column(name = "STATUS", nullable = false)
    private String status; // "ALLOW" / "BLOCK" 정도로 간단히

    private LocalDateTime firstSeenAt;
    private LocalDateTime lastSeenAt;
}

