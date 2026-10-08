package cn.sx.sxupr.simulator;

import cn.sx.sxupr.simulator.config.SimulatorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties(SimulatorProperties.class)
@SpringBootApplication
public class DeviceSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                DeviceSimulatorApplication.class,
                args
        );
    }
}