package cn.sx.sxupr.module.iot.server.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/iot/database")
public class DatabaseCheckController {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseCheckController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT 1",
                Integer.class
        );

        return Map.of(
                "database", "MySQL",
                "status", "UP",
                "result", result
        );
    }

}