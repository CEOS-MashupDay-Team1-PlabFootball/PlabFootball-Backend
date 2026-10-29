package com.ceos.plabfootball.global.config;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ReferenceDataInitializer implements ApplicationRunner {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager transactionManager;

    @Override
    public void run(ApplicationArguments args) {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            // 기존 데이터와 다른 ID/이름 충돌을 NULL 제약조건 오류로 검출하려면 strict SQL 모드가 필요하다.
            String sqlMode = jdbcTemplate.queryForObject("SELECT @@SESSION.sql_mode", String.class);
            if (sqlMode == null || (!sqlMode.contains("STRICT_TRANS_TABLES")
                    && !sqlMode.contains("STRICT_ALL_TABLES"))) {
                throw new IllegalStateException("기준 데이터 초기화에는 MySQL strict SQL 모드가 필요합니다.");
            }
            // 충돌 시 NULL을 대입해 실패시키므로 실제 운영 테이블에서도 NOT NULL이어야 한다.
            Integer requiredColumns = jdbcTemplate.queryForObject("""
                    SELECT COUNT(*) FROM information_schema.COLUMNS
                    WHERE TABLE_SCHEMA = DATABASE() AND COLUMN_NAME = 'name'
                      AND TABLE_NAME IN ('cities', 'areas') AND IS_NULLABLE = 'NO'
                    """, Integer.class);
            if (!Integer.valueOf(2).equals(requiredColumns)) {
                throw new IllegalStateException("cities.name과 areas.name은 NOT NULL 컬럼이어야 합니다.");
            }

            // 두 SQL 파일을 한 트랜잭션에서 실행한다. 충돌이 나면 새로 삽입한 데이터도 롤백한다.
            new ResourceDatabasePopulator(
                    new ClassPathResource("db/seed/01_cities.sql"),
                    new ClassPathResource("db/seed/02_areas.sql")).execute(dataSource);
        });
        log.info("City/area reference data initialization completed");
    }
}
