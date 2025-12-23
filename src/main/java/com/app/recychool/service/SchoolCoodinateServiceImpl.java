package com.app.recychool.service;

import com.app.recychool.domain.entity.School;
import com.app.recychool.exception.ReserveException;
import com.app.recychool.repository.SchoolCoodinateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SchoolCoodinateServiceImpl implements SchoolCoodinateService {

    private final SchoolCoodinateRepository schoolCoodinateRepository;

    @Override
    public School getSchoolCoordinate(Long schoolId) {
        return schoolCoodinateRepository.findById(schoolId)
                .orElseThrow(() -> new ReserveException("학교 정보를 찾을 수 없습니다."));
    }
}
