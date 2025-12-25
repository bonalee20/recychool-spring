package com.app.recychool.api.privateapi;

import com.app.recychool.domain.dto.ApiResponseDTO;
import com.app.recychool.domain.entity.Reserve;
import com.app.recychool.domain.entity.School;
import com.app.recychool.repository.ReserveRepository;
import com.app.recychool.service.SchoolService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/school")
public class SchoolApi {
    private final SchoolService schoolService;

    @PersistenceContext
    private EntityManager em;
    @GetMapping("/find-all")
    public ResponseEntity<ApiResponseDTO> getSchoolAll() {
        List<School> schoolAll = schoolService.getSchoolAll();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("학교조회", schoolAll));
    }

    @GetMapping("/parking-lot")
    public ResponseEntity<ApiResponseDTO> getParkingLot() {
        String sql =
                "SELECT * FROM (" +
                        "  SELECT s.* FROM TBL_SCHOOL s " +
                        "  WHERE s.SCHOOL_PARK_COUNT > 0 " +
                        "  ORDER BY DBMS_RANDOM.VALUE" +
                        ") WHERE ROWNUM <= 4";

        List<School> lists = em.createNativeQuery(sql, School.class).getResultList();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("학교 주차공간 조회", lists));
    }

    @GetMapping("/find-not-exist-school")
    public ResponseEntity<ApiResponseDTO> getNotExistSchool() {
        List<School> findPlaceReservationNotExistSchool = schoolService.getSchoolsWithoutPlaceReservation();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("장소예약 가능한 학교들 조회", findPlaceReservationNotExistSchool));
    }

    @GetMapping("/find-four-random")
    public ResponseEntity<ApiResponseDTO> getFourRandomSchool() {
        List<School> schools = schoolService.getFourRandomSchool();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("무료 영화 예약 가능한 학교들 조회", schools));
    }

    //장소 대여 검색
    @GetMapping("/search-place-by-region")
    public ResponseEntity<ApiResponseDTO> getSchoolsByRegion(@RequestParam("region") String region) {
        List<School> schools = schoolService.getSchoolsWithoutPlaceReservationByRegion(region);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("지역별 학교 검색 조회", schools));
    }
    //주차 검색
    @GetMapping("/search-parking-by-region")
    public ResponseEntity<ApiResponseDTO> getParkingSchoolsByRegion(@RequestParam("region") String region) {
        List<School> schools = schoolService.getSchoolsWithoutParkingReservationByRegion(region);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("지역별 학교 검색 조회", schools));
    }
}
