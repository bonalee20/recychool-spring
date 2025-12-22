package com.app.recychool.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SCHOOL_METADATA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SchoolMetadata {

    @Id
    @Column(name = "meta_key", length = 100)
    private String metaKey;

    @Column(name = "meta_value", length = 4000)
    private String metaValue;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    public void save(SchoolMetadata meta) {
    }
}
