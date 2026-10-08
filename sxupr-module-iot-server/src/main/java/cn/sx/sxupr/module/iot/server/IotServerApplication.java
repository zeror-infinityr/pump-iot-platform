package cn.sx.sxupr.module.iot.server;

import cn.sx.sxupr.module.iot.server.config.IotCommandProperties;
import cn.sx.sxupr.module.iot.server.config.IotMonitorProperties;
import cn.sx.sxupr.module.iot.server.infra.postgis.PostgisProperties;
import cn.sx.sxupr.module.iot.server.infra.tdengine.TdengineProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(
        {
                TdengineProperties.class,
                IotMonitorProperties.class,
                IotCommandProperties.class,
                PostgisProperties.class
        }
)
public class IotServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotServerApplication.class, args);
    }

}