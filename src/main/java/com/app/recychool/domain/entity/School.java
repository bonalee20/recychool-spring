package com.app.recychool.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    private String schoolCity; // 서울 || 경기
    @Column(unique = true)
    private String schoolName; //학교이름
    private Double schoolLand; //대지
    private Double schoolArea; // 건물연면적
    private String schoolPhone; //담당자전화번호
    private String schoolAddress; //학교주소
    private Double schoolLat; // 위도
    private Double schoolLon; // 경도
    private Integer schoolParkCount;


    private String schoolImagePath;
    private String schoolImageName;

    @OneToMany(mappedBy = "school")
    @JsonIgnore
    private List<MovieReservation> movieReservations;

}
