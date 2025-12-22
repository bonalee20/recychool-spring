package com.app.recychool.domain.dto.reserve;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolReservePageResponseDTO {

    // 학교 기본 정보
    private Long schoolId;
    private String schoolName;
    private String schoolAddress;
    private String schoolPhone;

    private Double schoolArea;
    private String schoolImageName;
    private String schoolImagePath;

    // 예약 정보 (URL에 따라 달라짐)
    private String reserveType;   // PLACE or PARKING
    private String usageTime;     // "09:00 ~ 17:00" / "18:00 ~ 08:00"
    private Integer price;        // PLACE = 0, PARKING = 30000
    private Integer deposit;      // PLACE = 50000, PARKING = 0

    private Double maxParkingCapacity;   // 총 수용 가능 대수
    private Integer currentParkingCount;  // 현재 예약된 대수


    // 캘린더 제어
    private List<LocalDate> unavailableDates; // PLACE 전용
    private Map<LocalDate, Integer> parkingCountMap; // PARKING 전용
}
