package com.app.recychool.repository;

import com.app.recychool.domain.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    boolean existsBySchoolName(String schoolName);
    Optional<School> findBySchoolName(String schoolName);
}
