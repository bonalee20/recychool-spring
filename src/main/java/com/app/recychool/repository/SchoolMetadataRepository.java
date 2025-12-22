package com.app.recychool.repository;

import com.app.recychool.domain.entity.SchoolMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolMetadataRepository extends JpaRepository<SchoolMetadata, String> {
}
