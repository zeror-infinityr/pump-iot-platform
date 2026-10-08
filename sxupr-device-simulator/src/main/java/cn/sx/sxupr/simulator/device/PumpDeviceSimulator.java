package cn.sx.sxupr.simulator.device;

import cn.sx.sxupr.module.iot.core.message.IotDevicePropertyMessage;
import cn.sx.sxupr.module.iot.core.topic.IotTopics;
import cn.sx.sxupr.simulator.config.SimulatorProperties;
import cn.sx.sxupr.simulator.mqtt.SimulatorMqttClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//@Component
//public class PumpDeviceSimulator {
//
//    private final SimulatorProperties properties;
//    private final SimulatorMqttClient mqttClient;
//    private final JsonMapper jsonMapper;
//
//    public PumpDeviceSimulator(
//            SimulatorProperties properties,
//            SimulatorMqttClient mqttClient,
//            JsonMapper jsonMapper) {
//
//        this.properties = properties;
//        this.mqttClient = mqttClient;
//        this.jsonMapper = jsonMapper;
//    }
//
//    @Scheduled(
//            fixedDelayString = "${simulator.interval-ms}",
//            initialDelay = 2000
//    )
//    public void reportProperties() {
//
//        try {
//
//            Map<String, Object> values =
//                    generateNormalValues();
//
//            IotDevicePropertyMessage message =
//                    new IotDevicePropertyMessage(
//                            UUID.randomUUID().toString(),
//                            properties.productKey(),
//                            properties.deviceName(),
//                            System.currentTimeMillis(),
//                            values
//                    );
//
//            String topic =
//                    IotTopics.propertyPost(
//                            properties.productKey(),
//                            properties.deviceName()
//                    );
//
//            String payload =
//                    jsonMapper.writeValueAsString(message);
//
//            mqttClient.publish(topic, payload);
//
//            System.out.println(
//                    "[DEVICE] publish topic="
//                            + topic
//                            + " payload="
//                            + payload
//            );
//
//        } catch (Exception e) {
//
//            System.err.println(
//                    "[DEVICE] 属性上报失败: "
//                            + e.getMessage()
//            );
//        }
//    }
//
//    private Map<String, Object> generateNormalValues() {
//
//        ThreadLocalRandom random =
//                ThreadLocalRandom.current();
//
//        double temperature =
//                round(
//                        random.nextDouble(
//                                55.0,
//                                70.0
//                        )
//                );
//
////        double waterLevel =
////                round(
////                        random.nextDouble(
////                                1.8,
////                                3.0
////                        )
////                );
//
//        double waterLevel;
//
//        if (properties.forceHighWater()) {
//
//            waterLevel =
//                    round(
//                            random.nextDouble(
//                                    3.4,
//                                    3.8
//                            )
//                    );
//
//        } else {
//
//            waterLevel =
//                    round(
//                            random.nextDouble(
//                                    1.8,
//                                    3.0
//                            )
//                    );
//        }
//
//        boolean pumpRunning =
//                random.nextDouble() > 0.15;
//
//        Map<String, Object> values =
//                new LinkedHashMap<>();
//
//        values.put(
//                "motorTemperature",
//                temperature
//        );
//
//        values.put(
//                "waterLevel",
//                waterLevel
//        );
//
//        values.put(
//                "pumpRunning",
//                pumpRunning
//        );
//
//        return values;
//    }
//
//    private double round(double value) {
//
//        return Math.round(value * 10.0) / 10.0;
//    }
//}


@Component
public class PumpDeviceSimulator {

    private final SimulatorProperties properties;
    private final SimulatorMqttClient mqttClient;
    private final JsonMapper jsonMapper;
    private final SamplingIntervalState intervalState;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public PumpDeviceSimulator(
            SimulatorProperties properties,
            SimulatorMqttClient mqttClient,
            JsonMapper jsonMapper,
            SamplingIntervalState intervalState) {

        this.properties = properties;
        this.mqttClient = mqttClient;
        this.jsonMapper = jsonMapper;
        this.intervalState = intervalState;
    }

    @PostConstruct
    public void start() {

        scheduleNext(2000);
    }

    private void scheduleNext(
            long delayMs) {

        scheduler.schedule(
                this::reportAndScheduleNext,
                delayMs,
                TimeUnit.MILLISECONDS
        );
    }

    private void reportAndScheduleNext() {

        try {

            reportProperties();

        } finally {

            scheduleNext(
                    intervalState.getIntervalMs()
            );
        }
    }

    @Scheduled(
            fixedDelayString = "${simulator.interval-ms}",
            initialDelay = 2000
    )
    public void reportProperties() {

        try {

            Map<String, Object> values =
                    generateNormalValues();

            IotDevicePropertyMessage message =
                    new IotDevicePropertyMessage(
                            UUID.randomUUID().toString(),
                            properties.productKey(),
                            properties.deviceName(),
                            System.currentTimeMillis(),
                            values
                    );

            String topic =
                    IotTopics.propertyPost(
                            properties.productKey(),
                            properties.deviceName()
                    );

            String payload =
                    jsonMapper.writeValueAsString(message);

            mqttClient.publish(topic, payload);

            System.out.println(
                    "[DEVICE] publish topic="
                            + topic
                            + " payload="
                            + payload
            );

        } catch (Exception e) {

            System.err.println(
                    "[DEVICE] 属性上报失败: "
                            + e.getMessage()
            );
        }
    }

        private Map<String, Object> generateNormalValues() {

        ThreadLocalRandom random =
                ThreadLocalRandom.current();

        double temperature =
                round(
                        random.nextDouble(
                                55.0,
                                70.0
                        )
                );

    //        double waterLevel =
    //                round(
    //                        random.nextDouble(
    //                                1.8,
    //                                3.0
    //                        )
    //                );

        double waterLevel;

        if (properties.forceHighWater()) {

            waterLevel =
                    round(
                            random.nextDouble(
                                    3.4,
                                    3.8
                            )
                    );

        } else {

            waterLevel =
                    round(
                            random.nextDouble(
                                    1.8,
                                    3.0
                            )
                    );
        }

        boolean pumpRunning =
                random.nextDouble() > 0.15;

        Map<String, Object> values =
                new LinkedHashMap<>();

        values.put(
                "motorTemperature",
                temperature
        );

        values.put(
                "waterLevel",
                waterLevel
        );

        values.put(
                "pumpRunning",
                pumpRunning
        );

        return values;
    }

    private double round(double value) {

        return Math.round(value * 10.0) / 10.0;
    }

    @PreDestroy
    public void stop() {

        scheduler.shutdownNow();
    }
}