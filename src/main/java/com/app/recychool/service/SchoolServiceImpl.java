package com.app.recychool.service;

import com.app.recychool.domain.dto.SchoolDTO;
import com.app.recychool.domain.entity.School;
import com.app.recychool.repository.SchoolRepository;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class SchoolServiceImpl implements SchoolService {
    private final SchoolRepository schoolRepository;

    @Override
    public List<School> getSchoolAll() {
        return schoolRepository.findAll();
    }

    @Override
    public List<School> getSchoolsWithoutPlaceReservation() { return schoolRepository.findSchoolsWithoutPlaceReservation();}

    @Override
    public List<School> getFourRandomSchool() { return schoolRepository.findFourRandomSchool();}

    @Override
    public List<School> getSchoolsWithoutPlaceReservationByRegion(String region) {
        // 레포지토리에서 만든 메서드를 호출
        return schoolRepository.findSchoolsWithoutPlaceReservationByRegion(region);
    }


    @Override
    public List<School> getSchoolsWithoutParkingReservationByRegion(String region) {
        return schoolRepository.findSchoolsWithoutParkingReservationByRegion(region);
    }
}
