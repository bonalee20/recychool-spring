package com.app.recychool.service;

import com.app.recychool.domain.entity.School;
import com.app.recychool.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchoolBulkLoadService {

    private final SchoolRepository schoolRepository;
    private final PlatformTransactionManager transactionManager;

    public record LoadResult(int inserted, int skipped, int failed) {}

    public LoadResult loadFromInputStream(InputStream is, int batchSize) {
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        int inserted = 0, skipped = 0, failed = 0;

        // 기존 이름을 한 번에 조회(대소문자/공백 트림)
        Set<String> existing = new HashSet<>(schoolRepository.findAll()
                .stream()
                .map(s -> Optional.ofNullable(s.getSchoolName()).orElse("").trim())
                .collect(Collectors.toSet()));

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            // 헤더 읽기 (첫 줄)
            String header = br.readLine();
            if (header == null) {
                return new LoadResult(0, 0, 0);
            }

            List<School> buffer = new ArrayList<>(batchSize);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                // CSV는 콤마 구분. 값에 콤마가 포함될 가능성이 있으면 CSV 라이브러리 사용 권장.
                String[] cols = line.split(",", -1); // -1: 빈 칸도 배열에 포함

                String city = safe(cols, 0);
                String name = safe(cols, 1);
                String landStr = safe(cols, 2);
                String phone = safe(cols, 3);
                String address = safe(cols, 4);
                String lonStr = safe(cols, 5); // _X
                String latStr = safe(cols, 6); // _Y
                String areaStr = safe(cols, 7);
                String imgPath = safe(cols, 8);
                String imgName = safe(cols, 9);

                if (name == null || name.isBlank()) {
                    skipped++;
                    continue;
                }

                // 이미 DB에 존재하면 스킵
                if (existing.contains(name)) {
                    skipped++;
                    continue;
                }

                Double land = parseDoubleOrNull(landStr);
                Double area = parseDoubleOrNull(areaStr);
                Double lon = parseDoubleOrNull(lonStr);
                Double lat = parseDoubleOrNull(latStr);

                School s = School.builder()
                        .schoolCity(city)
                        .schoolName(name)
                        .schoolLand(land)
                        .schoolArea(area)
                        .schoolPhone(phone)
                        .schoolAddress(address)
                        .schoolLon(lon)
                        .schoolLat(lat)
                        .schoolImagePath(imgPath)
                        .schoolImageName(imgName)
                        .build();

                buffer.add(s);
                existing.add(name); // 같은 실행 내 중복 방지

                if (buffer.size() >= batchSize) {
                    int[] result = saveBatch(txTemplate, buffer);
                    inserted += result[0];
                    failed += result[1];
                    buffer.clear();
                }
            }
            if (!buffer.isEmpty()) {
                int[] result = saveBatch(txTemplate, buffer);
                inserted += result[0];
                failed += result[1];
                buffer.clear();
            }

        } catch (Exception e) {
            throw new RuntimeException("CSV 읽기 실패", e);
        }
        return new LoadResult(inserted, skipped, failed);
    }

    private int[] saveBatch(TransactionTemplate txTemplate, List<School> batch) {
        final int[] result = new int[2]; // [inserted, failed]
        txTemplate.executeWithoutResult(status -> {
            try {
                schoolRepository.saveAll(batch);
                result[0] = batch.size();
            } catch (Exception e) {
                // 배치 실패 시 개별로 시도
                int failed = 0;
                for (School s : batch) {
                    try {
                        schoolRepository.save(s);
                    } catch (Exception ex) {
                        failed++;
                    }
                }
                result[0] = batch.size() - failed;
                result[1] = failed;
            }
        });
        return result;
    }

    private String safe(String[] arr, int idx) {
        if (idx >= arr.length) return null;
        String v = arr[idx].trim();
        if (v.isEmpty() || v.equalsIgnoreCase("#N/A")) return null;
        return v;
    }
    private Double parseDoubleOrNull(String s) {
        if (s == null) return null;
        try { return Double.parseDouble(s); } catch (Exception e) { return null; }
    }

}
