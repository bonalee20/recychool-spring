package com.app.recychool.util;

import com.app.recychool.domain.entity.SchoolMetadata;
import com.app.recychool.repository.SchoolMetadataRepository;
import com.app.recychool.service.SchoolBulkLoadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchoolLoader {

    private final ResourceLoader resourceLoader;
    private final SchoolMetadataRepository schoolMetadataRepo;
    private final SchoolBulkLoadService schoolBulkLoadService;

    @Value("${app.school-data-path:classpath:school/폐교데이터_좌표삽입.csv}")
    private String dataPath;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // 마커 체크
        if (schoolMetadataRepo.existsById("schools_loaded")) {
            System.out.println("학교 데이터 이미 적재됨. 스킵합니다.");
            return;
        }

        try {
            Resource resource = resourceLoader.getResource(dataPath);
            if (!resource.exists()) {
                System.err.println("데이터 파일을 찾을 수 없습니다: " + dataPath);
                return;
            }

            var result = schoolBulkLoadService.loadFromInputStream(resource.getInputStream(), 100);
            System.out.println("학교 적재 결과 inserted=" + result.inserted() + ", skipped=" + result.skipped() + ", failed=" + result.failed());

            // 성공 시 마커 저장
            SchoolMetadata meta = SchoolMetadata.builder()
                    .metaKey("schools_loaded")
                    .metaValue("true")
                    .createAt(LocalDateTime.now())
                    .build();
            meta.save(meta);
            System.out.println("마커 저장 완료");
        } catch (Exception e) {
            System.err.println("초기 학교 적재 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

