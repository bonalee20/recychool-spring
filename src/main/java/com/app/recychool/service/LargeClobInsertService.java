package com.app.recychool.service;

import com.app.recychool.repository.PolygonsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LargeClobInsertService {

    private static final Logger log = LoggerFactory.getLogger(LargeClobInsertService.class);

    private final JdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager txManager;
    private final PolygonsRepository  polygonsRepository;
    private final DefaultLobHandler lobHandler = new DefaultLobHandler();

    private final String INSERT_SQL = "INSERT INTO POLYGON_GEOJSON (NAME, GEOJSON) VALUES (?, ?)";

    public void insertFilesInBatches(List<File> files, int batchSize) {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        List<String> existing = polygonsRepository.findAllPolygonNames();
        Set<String> existingSet = new HashSet<>(existing);

        List<File> toInsert = files.stream()
                .filter(file -> {
                    String name = file.getName().replace(".geojson", "");
                    if(existingSet.contains(name)) {
                        log.info("중복된 파일: {}", name);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        if(toInsert.isEmpty()) {
            log.info("삽입 대상 파일이 없습니다.");
            return;
        }

        for (int i = 0; i < toInsert.size(); i += batchSize) {
            int end = Math.min(i + batchSize, toInsert.size());
            List<File> sub = toInsert.subList(i, end);
            log.info("Starting batch insert: files {}..{} (count={})", i, end - 1, sub.size());

            txTemplate.executeWithoutResult(status -> {
                for (File f : sub) {
                    String name = f.getName().replace(".geojson", "");
                    try {
                        if(existingSet.contains(name)) {
                            log.info("이미 존재하므로 스킵합니다: {}", name);
                            continue;
                        }
                        insertSingleFileStreaming(f);
                        log.info("파일 삽입: {}", f.getName());
                        existingSet.add(name);
                    } catch (DataIntegrityViolationException dive) {
                        // 배치 내 개별 파일 실패는 로그로 남기고 계속 진행하거나 필요시 예외를 던져 배치 롤백
                        log.error("Failed to insert file {}: {}", f.getName(), dive.getMessage());
                        // 만약 배치 전체를 롤백하려면 throw new RuntimeException(ex);
                    } catch (Exception e) {
                        log.error("파일 삽입 실패: {}", f.getName(), e.getMessage());
                    }
                }
            });

            log.info("Finished batch {}..{}", i, end - 1);
        }
    }

    public void insertSingleFileStreaming(File file) {
        String name = file.getName().replace(".geojson", "");

        try (Reader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            jdbcTemplate.execute((ConnectionCallback<Integer>) connection -> {
                try (PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
                    ps.setString(1, file.getName().replace(".geojson", ""));
                    LobCreator lobCreator = lobHandler.getLobCreator();
                    // 드라이버에 따라 길이 파라미터를 (int)file.length() 또는 -1로 시도
                    int length = (int) file.length(); // 대부분 동작
                    lobCreator.setClobAsCharacterStream(ps, 2, reader, length);
                    return ps.executeUpdate();
                }
            });
        }catch (DataIntegrityViolationException dive) {
            log.warn("중복 제약 제한: {}: {}", name, dive.getMessage());
            throw dive;
        }catch (Exception e) {
            throw new RuntimeException("파일 삽입 실패: " + file.getName(), e);
        }
    }
}
