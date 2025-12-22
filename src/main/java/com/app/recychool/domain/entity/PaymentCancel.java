package com.app.recychool.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "TBL_PAYMENT_CANCEL")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "SEQ_PAYMENT_CANCEL_GENERATOR",
        sequenceName = "SEQ_PAYMENT_CANCEL",
        allocationSize = 1
)
public class PaymentCancel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_PAYMENT_CANCEL_GENERATOR")
    @EqualsAndHashCode.Include
    private Long id;
    private Integer paymentCancelPrice;
    private Date paymentCancelDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_ID", unique = true)
    private Payment payment;

}
