package com.app.recychool.config.p6spy;

import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.util.StringUtils;

import java.sql.SQLException;

public class P6SpyFormatter extends JdbcEventListener implements MessageFormattingStrategy {
    @Override
    public void onAfterGetConnection(ConnectionInformation connectionInformation, SQLException e) {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(getClass().getName());
    }

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category,
                                String prepared, String sql, String url) {
        if (!StringUtils.hasText(sql)) {
            return "";
        }

        String lowerSql = sql.toLowerCase().trim();

        // 특정 쿼리 필터링 (글로벌 임시 테이블 생성 쿼리 제외)
        if(sql != null && sql.toLowerCase().contains("create global temporary table")) {
            return "";  // 로그 출력하지 않음
        }

        // Hibernate 내부 조회 쿼리 필터링
        if (lowerSql.contains("from all_sequences") ||
                lowerSql.contains("from dual") ||
                lowerSql.contains("hibernate_sequence") ||
                (lowerSql.startsWith("select") && !lowerSql.contains("tbl_"))) {
            return "";
        }

        // 로그 출력
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(category).append(" ").append(elapsed).append("ms").append("\n\t");
        sb.append(highlight(format(sql)));

        return sb.toString();
    }


    private String format(String sql){
        if(isDDL(sql)){
            return FormatStyle.DDL.getFormatter().format(sql);
        }else if(isBasic(sql)){
            return FormatStyle.BASIC.getFormatter().format(sql);
        }
        return sql;
    }

    private String highlight(String sql){
        return FormatStyle.HIGHLIGHT.getFormatter().format(sql);
    }

    private boolean isDDL(String sql){
        String lower = sql.trim().toLowerCase();
        return lower.startsWith("create") || lower.startsWith("alter") || lower.startsWith("comment");
    }

    private boolean isBasic(String sql){
        String lower = sql.trim().toLowerCase();
        return lower.startsWith("select") || lower.startsWith("insert") || lower.startsWith("update") || lower.startsWith("delete");
    }

}
