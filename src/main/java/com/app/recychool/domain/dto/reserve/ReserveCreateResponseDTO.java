package com.app.recychool.domain.dto.reserve;

import com.app.recychool.domain.enums.ReserveStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveCreateResponseDTO {

    private Long reserveId;
    private ReserveStatus reserveStatus;

    private Integer price;
    private Integer deposit;

    private Integer waitingOrder; // PARKING 대기일 때만 값 있음
}
