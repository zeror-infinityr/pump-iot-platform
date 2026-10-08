package cn.sx.sxupr.module.iot.server.infra.postgis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "postgis")
public record PostgisProperties(

        String url,

        String username,

        String password,

        String driverClassName

) {
}