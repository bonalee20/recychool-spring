package com.app.recychool.service;

import com.app.recychool.domain.entity.School;

import java.util.List;

public interface SchoolService {
    public List<School> getSchoolAll();
    public List<School> getSchoolsWithoutPlaceReservation();
    public List<School> getFourRandomSchool();
}
