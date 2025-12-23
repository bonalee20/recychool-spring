package com.app.recychool.domain.dto;

import com.app.recychool.domain.entity.School;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SchoolDTO {
    Long id;
    String schoolName;
    String schoolParkCount;

    public SchoolDTO(School school, Long id, String schoolName, String schoolParkCount) {
        this.id = id;
        this.schoolName = schoolName;
        this.schoolParkCount = schoolParkCount;
    }
}
