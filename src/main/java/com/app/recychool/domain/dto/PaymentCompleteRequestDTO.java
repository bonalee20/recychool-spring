package com.app.recychool.domain.dto;

import com.app.recychool.domain.type.PaymentType;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCompleteRequestDTO {

    private Long reserveId;
    private String impUid;
    private String merchantUid;
    private PaymentType paymentType;


}
