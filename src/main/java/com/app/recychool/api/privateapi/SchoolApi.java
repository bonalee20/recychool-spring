package com.app.recychool.api.privateapi;

import com.app.recychool.domain.dto.ApiResponseDTO;
import com.app.recychool.domain.entity.School;
import com.app.recychool.service.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/school/*")
public class SchoolApi {
    private final SchoolService schoolService;
    @GetMapping("get-SchoolAll")
    public ResponseEntity<ApiResponseDTO> getSchoolAll() {
        List<School> schoolAll = schoolService.getSchoolAll();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.of("학교조회", schoolAll));
    }


}
