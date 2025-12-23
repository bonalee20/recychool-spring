package com.app.recychool.repository;

import com.app.recychool.domain.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolCoodinateRepository extends JpaRepository<School, Long> {
}
