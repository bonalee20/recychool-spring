package com.app.recychool.domain.entity;

import com.app.recychool.domain.type.PaymentStatus;
import com.app.recychool.domain.type.PaymentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_PAYMENT")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "SEQ_PAYMENT_GENERATOR",
        sequenceName = "SEQ_PAYMENT",
        allocationSize = 1
)

public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_PAYMENT_GENERATOR")
    @EqualsAndHashCode.Include
    private Long id;
    private Integer paymentPrice;

    @Column(name = "IMP_UID", nullable = false, unique = true)
    private String impUid;

    @Column(name = "MERCHANT_UID", nullable = false)
    private String merchantUid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;


//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "RESERVE_ID")
//    private Reserve reserve;


}
