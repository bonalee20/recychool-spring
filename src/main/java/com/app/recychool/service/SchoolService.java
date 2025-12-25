package com.app.recychool.service;

import com.app.recychool.domain.entity.School;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface SchoolService {
    public List<School> getSchoolAll();
    public List<School> getSchoolsWithoutPlaceReservation();
    public List<School> getFourRandomSchool();

    //
    public List<School> getSchoolsWithoutPlaceReservationByRegion(String region);
    public List<School> getSchoolsWithoutParkingReservationByRegion(String name);
}
