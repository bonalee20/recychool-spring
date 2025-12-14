package com.app.recychool.repository;

import com.app.recychool.domain.entity.IdentityVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdentityVerificationRepository extends JpaRepository<IdentityVerification, Long> {
    Optional<IdentityVerification> findByReceiptId(String receiptId);
}
