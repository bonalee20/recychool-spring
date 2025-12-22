package com.app.recychool.api.publicapi;


import com.app.recychool.domain.entity.Polygons;
import com.app.recychool.repository.PolygonsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/polygons")
@RequiredArgsConstructor
public class PolygonAPI {

    private final PolygonsRepository polygonsRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/find")
    public List<String> listGeoJson() {
        return polygonsRepository.findAll().stream().map(Polygons::getGeoJson).collect(Collectors.toList());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Polygons create(@RequestBody CreateDto dto) {
        Polygons polygons = Polygons.builder().polygonName(dto.name()).geoJson(dto.geoJson()).build();
       return polygonsRepository.save(polygons);
    }

    public record CreateDto(String name, String geoJson) {}

}
