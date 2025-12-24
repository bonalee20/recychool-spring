package com.app.recychool.service;

import com.app.recychool.domain.entity.School;
import com.app.recychool.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
