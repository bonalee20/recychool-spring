package com.app.recychool.domain.entity;

import com.app.recychool.domain.enums.IdentityProvider;
import com.app.recychool.domain.enums.VerificationPurpose;
import com.app.recychool.domain.enums.VerificationStatus;
import com.nimbusds.jose.crypto.impl.HMAC;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_IDENTITY_VERIFICATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "SEQ_IDENTITY_VERIFICATION_GENERATOR",
        sequenceName = "SEQ_IDENTITY_VERIFICATION",
        allocationSize = 1
)
@ToString(onlyExplicitlyIncluded = true)
public class IdentityVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_IDENTITY_VERIFICATION_GENERATOR")
    private Long id;

    @Column(name = "IDENTITY_VERIFICATION_PROVIDER")
    @Enumerated(EnumType.STRING)
    private IdentityProvider identityProvider;

    @Column(name = "IDENTITY_VERIFICATION_PRUPOSE\n")
    @Enumerated(EnumType.STRING)
    private VerificationPurpose verificationPurpose;

    @Column(name = "IDENTITY_VERIFICATION_STATUS")
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    private String receiptId;
    private String identityKey;
    private String tokenHash;
    private LocalDateTime tokenIssuedAt;
    private LocalDateTime tokenExpiresAt;
}
