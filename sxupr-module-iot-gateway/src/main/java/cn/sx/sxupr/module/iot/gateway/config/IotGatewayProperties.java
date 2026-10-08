package cn.sx.sxupr.module.iot.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iot.gateway")
public record IotGatewayProperties(

        String brokerUri,

        String serverBaseUrl

) {
}