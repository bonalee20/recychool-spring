package com.app.recychool.domain.dto;


import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPageResponseDTO {

    // Reserve
    private Long reserveId;
    private String reserveType;
    private String startDate;
    private String endDate;

    // 결제 금액
    private Integer amount;

    private Long SchoolId;


    private String userName;
    private String userEmail;
    private String userPhone;
    private String schoolName;
    private String schoolAddress;

}
