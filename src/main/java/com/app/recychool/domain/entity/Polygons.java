package com.app.recychool.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TBL_POLYGONS")
@Builder
public class Polygons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, unique = true)
    private String polygonName;

    @Lob
    @Column(name = "GEOJSON", columnDefinition = "CLOB")
    private String geoJson;

    public String getGeoJson() {
        return geoJson;
    }
    public void setGeoJson(String geoJson) {
        this.geoJson = geoJson;
    }
}
