package cn.sx.sxupr.module.iot.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iot.command")
public record IotCommandProperties(

        long timeoutSeconds

) {
}