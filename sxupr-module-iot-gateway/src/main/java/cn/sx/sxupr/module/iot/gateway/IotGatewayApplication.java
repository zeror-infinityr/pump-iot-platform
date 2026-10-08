package cn.sx.sxupr.module.iot.gateway;

import cn.sx.sxupr.module.iot.gateway.config.IotGatewayProperties;
import cn.sx.sxupr.module.iot.gateway.grpc.IotGrpcClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(
        {
                IotGatewayProperties.class,
                IotGrpcClientProperties.class
        })
@SpringBootApplication
public class IotGatewayApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                IotGatewayApplication.class,
                args
        );
    }
}