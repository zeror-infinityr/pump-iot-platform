package cn.sx.sxupr.module.iot.server.infra.tdengine;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TdengineClient {

    private final HikariDataSource dataSource;

    private final JdbcTemplate jdbcTemplate;

    public TdengineClient(
            TdengineProperties properties) {

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
                "pump-iot-tdengine-pool"
        );

        config.setMaximumPoolSize(5);

        config.setMinimumIdle(1);

        config.setConnectionTimeout(5000);

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