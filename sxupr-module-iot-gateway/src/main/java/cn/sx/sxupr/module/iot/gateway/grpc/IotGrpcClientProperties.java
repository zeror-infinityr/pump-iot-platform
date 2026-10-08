package cn.sx.sxupr.module.iot.gateway.grpc;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(
        prefix = "iot.grpc.client"
)
public record IotGrpcClientProperties(

        String host,

        int port,

        long deadlineMs

) {
}