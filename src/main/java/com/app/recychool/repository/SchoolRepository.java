package com.app.recychool.repository;

import com.app.recychool.domain.entity.School;
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
}
