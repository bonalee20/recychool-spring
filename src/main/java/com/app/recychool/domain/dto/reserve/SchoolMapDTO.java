package com.app.recychool.domain.dto.reserve;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolMapDTO {
    private Long schoolId;
    private String schoolName;
    private Double x;
    private Double y;
}
