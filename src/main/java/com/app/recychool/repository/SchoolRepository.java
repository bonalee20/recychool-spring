package com.app.recychool.repository;

import com.app.recychool.domain.entity.School;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    boolean existsBySchoolName(String schoolName);
    Optional<School> findBySchoolName(String schoolName);

    @Query(value = "SELECT t FROM ( SELECT s FROM TBL_School s WHERE s.schoolParkCount > 0 order by dbms_random.value ) t WHERE ROWNUM <= 4", nativeQuery = true)
    List<School> findRandomSchoolWithParkLimit();

    @Query(value = "SELECT * FROM ( SELECT s.* FROM TBL_SCHOOL s WHERE NOT EXISTS( SELECT 1 FROM TBL_RESERVE r WHERE r.SCHOOL_ID = s.ID AND r.RESERVE_TYPE = 'PLACE' ) ORDER BY DBMS_RANDOM.VALUE(1, 4) ) WHERE ROWNUM <= 4",  nativeQuery = true)
    List<School> findSchoolsWithoutPlaceReservation();

    @Query(value = "SELECT * FROM ( SELECT s.* FROM TBL_SCHOOL s ORDER BY DBMS_RANDOM.VALUE(1, 4) ) WHERE ROWNUM <= 4", nativeQuery = true)
    List<School> findFourRandomSchool();


    //검색 후 나오는 학교들 ( place )
    @Query(value = """
    SELECT * FROM (
        SELECT s.* FROM TBL_SCHOOL s 
        WHERE NOT EXISTS (
            SELECT 1 
            FROM TBL_RESERVE r 
            WHERE r.SCHOOL_ID = s.ID AND r.RESERVE_TYPE = 'PLACE'
        )
        AND s.SCHOOL_ADDRESS LIKE '%' || :region || '%'
    ) 
    WHERE ROWNUM <= 4
    """, nativeQuery = true)
    List<School> findSchoolsWithoutPlaceReservationByRegion(String region);

    @Query(value = """
    SELECT * FROM (
        SELECT s.* FROM TBL_SCHOOL s 
        WHERE NOT EXISTS (
            SELECT 1 
            FROM TBL_RESERVE r 
            WHERE r.SCHOOL_ID = s.ID AND r.RESERVE_TYPE = 'PARKING'
        )
        AND s.SCHOOL_ADDRESS LIKE '%' || :region || '%'
    ) 
    WHERE ROWNUM <= 4
    """, nativeQuery = true)
    List<School> findSchoolsWithoutParkingReservationByRegion(String region);



}
