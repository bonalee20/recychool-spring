package com.app.recychool.repository;

import com.app.recychool.domain.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    public School save(School school);
    public School findById(long id);
    public List<School> findAll();
}
