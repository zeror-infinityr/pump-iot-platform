package cn.sx.sxupr.module.iot.server.kafka;

import cn.sx.sxupr.module.iot.core.kafka.IotKafkaTopics;
import cn.sx.sxupr.module.iot.core.message.IotDeviceCommandMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.concurrent.TimeUnit;

@Component
public class DeviceCommandKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final JsonMapper jsonMapper;

    public DeviceCommandKafkaProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            JsonMapper jsonMapper) {

        this.kafkaTemplate = kafkaTemplate;
        this.jsonMapper = jsonMapper;
    }

    public void send(
            IotDeviceCommandMessage message) {

        try {

            String payload =
                    jsonMapper.writeValueAsString(message);

            String key =
                    message.productKey()
                            + ":"
                            + message.deviceName();

            kafkaTemplate.send(
                    IotKafkaTopics.DEVICE_COMMAND_DOWNSTREAM,
                    key,
                    payload
            ).get(
                    5,
                    TimeUnit.SECONDS
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "设备命令发送 Kafka 失败",
                    e
            );
        }
    }
}