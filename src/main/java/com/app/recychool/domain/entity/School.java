package com.app.recychool.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_SCHOOL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "SEQ_SCHOOL_GENERATOR",
        sequenceName = "SEQ_SCHOOL",
        allocationSize = 1
)
public class School {
    //시군구명 폐교명 건물연면적 대지 담당자 전화번호	소재지도로명주소
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_SCHOOL_GENERATOR")
    private Long id;
    private String schoolCity;
    private String schoolName;
    private Integer schoolLand; //대지
    private Integer schoolArea; //건물연면적
    private String schoolPhone;
    private String schoolAddress;

    private String schoolImagePath;
    private String schoolImageName;


}
