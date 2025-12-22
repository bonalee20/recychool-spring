package com.app.recychool.repository;

import com.app.recychool.domain.entity.Polygons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PolygonsRepository extends JpaRepository<Polygons, Long> {

    @Query(value = "SELECT SDO_UTIL.TO_GEOJSON(p.GEOM) FROM POLYGONS p ", nativeQuery = true)
    List<Polygons> findAllGeomsAsGeoJson();

    boolean existsByPolygonName(String polygonName);

    @Query("select p.polygonName from Polygons p")
    List<String> findAllPolygonNames();
}
