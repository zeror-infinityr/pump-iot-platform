package cn.sx.sxupr.simulator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "simulator")
public record SimulatorProperties(

        String brokerUri,

        String productKey,

        String deviceName,

        boolean forceHighWater,

        long intervalMs

) {
}