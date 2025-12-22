package com.app.recychool.util;

import com.app.recychool.domain.entity.Polygons;
import com.app.recychool.repository.PolygonsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GeoJsonBulkLoader implements CommandLineRunner {

    private final PolygonsRepository polygonsRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:polygons/*.geojson");

        Set<String> existing = new HashSet<>(polygonsRepository.findAllPolygonNames());

        int inserted = 0, skipped = 0;

        for(Resource resource : resources){
            String name = resource.getFilename() != null ? resource.getFilename().replace(".geojson", "") : "";
            if(existing.contains(name)){
                skipped++;
                continue;
            }
            try (Reader reader = new BufferedReader(Files.newBufferedReader(resource.getFile().toPath(), StandardCharsets.UTF_8))) {
                String content = new BufferedReader(Files.newBufferedReader(resource.getFile().toPath(), StandardCharsets.UTF_8))
                        .lines().collect(Collectors.joining());

                Polygons polygons = Polygons.builder()
                        .polygonName(name)
                        .geoJson(content)
                        .build();

                polygonsRepository.save(polygons);
                existing.add(name);
                inserted++;
            } catch (Exception e) {
                throw new Exception("파일 삽입 실패");
            }
        }
    }
}
