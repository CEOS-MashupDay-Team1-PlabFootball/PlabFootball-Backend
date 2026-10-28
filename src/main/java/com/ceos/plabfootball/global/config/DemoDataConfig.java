package com.ceos.plabfootball.global.config;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Slf4j
@Configuration
@Profile("demo")
public class DemoDataConfig {
    @Bean
    public ApplicationRunner seedDemoData(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        return args -> {
            // 기존 팀 데이터가 있는 DB에는 시연 데이터를 자동 삽입하지 않는다.
            Long matches = jdbcTemplate.queryForObject("select count(*) from matches", Long.class);
            Long stadiums = jdbcTemplate.queryForObject("select count(*) from stadiums", Long.class);
            Long groups = jdbcTemplate.queryForObject("select count(*) from stadium_groups", Long.class);
            if ((matches != null && matches > 0) || (stadiums != null && stadiums > 0)
                    || (groups != null && groups > 0)) {
                log.info("Skipping demo seed: matches, stadiums or stadium groups already exist");
                return;
            }
            new ResourceDatabasePopulator(
                    new ClassPathResource("db/seed/01_cities.sql"),
                    new ClassPathResource("db/seed/02_areas.sql"),
                    new ClassPathResource("db/seed/03_demo.sql")).execute(dataSource);
            log.info("Demo seed loaded: 32 stadiums and 451 matches");
        };
    }
}
