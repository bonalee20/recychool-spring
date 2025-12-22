package com.app.recychool.service;

import com.app.recychool.domain.entity.Polygons;
import com.app.recychool.repository.PolygonsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkLoadService {

    private final PolygonsRepository polygonsRepository;
    private final PlatformTransactionManager ptManager;

    @PersistenceContext
    private EntityManager em;

    public void loadFromClasspathPolygons(int batchSize) throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:polygons/*.geojson");
        List<String> existing = polygonsRepository.findAllPolygonNames();
        Set<String> existingSet = new HashSet<>(existing);

        TransactionTemplate txTemplate = new TransactionTemplate(ptManager);

        List<Polygons> buffer = new ArrayList<>(batchSize);

        for (Resource r : resources) {
            String content = new String(r.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String name = r.getFilename() != null ? r.getFilename().replace(".geojson", "") : "unknown";
            if(existingSet.contains(name)){
                log.info("이미 존재하는 파일: {}", name);
                continue;
            }
            Polygons e = Polygons.builder()
                    .polygonName(name)
                    .geoJson(content)
                    .build();

            buffer.add(e);

            if (buffer.size() >= batchSize) {
                // 배치 저장: 각 배치는 별도 트랜잭션으로 처리
                saveBatch(txTemplate, buffer);
                buffer.clear();
            }
        }

        // 남은 버퍼 저장
        if (!buffer.isEmpty()) {
            saveBatch(txTemplate, buffer);
            buffer.clear();
        }
    }
    private void saveBatch(TransactionTemplate txTemplate, List<Polygons> batch) {
        txTemplate.executeWithoutResult(status -> {
            polygonsRepository.saveAll(batch);
            // 강제 flush + 영속성 컨텍스트 clear로 메모리 누수 방지
            em.flush();
            em.clear();
        });
    }

}
