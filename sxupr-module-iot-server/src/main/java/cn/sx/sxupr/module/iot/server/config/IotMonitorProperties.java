package cn.sx.sxupr.module.iot.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "iot.monitor")
public record IotMonitorProperties(

        long offlineTimeoutSeconds,

        BigDecimal highWaterThreshold,

        int highWaterConsecutiveCount

) {
}