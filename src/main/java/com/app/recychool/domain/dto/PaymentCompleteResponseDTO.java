package com.app.recychool.domain.dto;

import com.app.recychool.domain.type.PaymentStatus;
import com.app.recychool.domain.type.PaymentType;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCompleteResponseDTO {

    private Long paymentId;
    private Long reserveId;
    private PaymentStatus paymentStatus;
    private Integer amount;

}
