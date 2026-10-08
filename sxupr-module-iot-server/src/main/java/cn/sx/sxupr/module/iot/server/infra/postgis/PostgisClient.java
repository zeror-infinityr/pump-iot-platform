package cn.sx.sxupr.module.iot.server.infra.postgis;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostgisClient {

    private final HikariDataSource dataSource;

    private final JdbcTemplate jdbcTemplate;

    public PostgisClient(
            PostgisProperties properties) {

        HikariConfig config =
                new HikariConfig();

        config.setJdbcUrl(
                properties.url()
        );

        config.setUsername(
                properties.username()
        );

        config.setPassword(
                properties.password()
        );

        config.setDriverClassName(
                properties.driverClassName()
        );

        config.setPoolName(
                "pump-iot-postgis-pool"
        );

        config.setMaximumPoolSize(5);

        config.setMinimumIdle(1);

        this.dataSource =
                new HikariDataSource(config);

        this.jdbcTemplate =
                new JdbcTemplate(dataSource);
    }

    public JdbcTemplate jdbcTemplate() {
        return jdbcTemplate;
    }

    @PreDestroy
    public void close() {
        dataSource.close();
    }
}