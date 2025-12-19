package com.app.recychool.repository;

import com.app.recychool.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
//    boolean existsByImpUid(String impUid);
//    boolean existsByReserve_Id(Long reserveId);
//    Optional<Payment> findByReserve_Id(Long reserveId);
}
