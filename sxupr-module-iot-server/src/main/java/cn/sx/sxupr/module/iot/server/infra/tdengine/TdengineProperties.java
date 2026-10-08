package cn.sx.sxupr.module.iot.server.infra.tdengine;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tdengine")
public record TdengineProperties(

        String url,

        String username,

        String password,

        String driverClassName

) {
}