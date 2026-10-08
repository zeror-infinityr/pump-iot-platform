package cn.sx.sxupr.module.iot.gateway.kafka;

import cn.sx.sxupr.module.iot.core.kafka.IotKafkaTopics;
import cn.sx.sxupr.module.iot.core.message.IotDevicePropertyMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class DeviceMessageKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final JsonMapper jsonMapper;

    public DeviceMessageKafkaProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            JsonMapper jsonMapper) {

        this.kafkaTemplate = kafkaTemplate;
        this.jsonMapper = jsonMapper;
    }

    public void send(
            IotDevicePropertyMessage message) {

        try {

            String payload =
                    jsonMapper.writeValueAsString(message);

            String key =
                    message.productKey()
                            + ":"
                            + message.deviceName();

            kafkaTemplate.send(
                    IotKafkaTopics.DEVICE_PROPERTY_UPSTREAM,
                    key,
                    payload
            ).whenComplete(
                    (result, exception) -> {

                        if (exception != null) {

                            System.err.println(
                                    "[KAFKA] send failed requestId="
                                            + message.requestId()
                                            + ", error="
                                            + exception.getMessage()
                            );

                            return;
                        }

                        System.out.println(
                                "[KAFKA] sent requestId="
                                        + message.requestId()
                                        + ", partition="
                                        + result.getRecordMetadata()
                                        .partition()
                                        + ", offset="
                                        + result.getRecordMetadata()
                                        .offset()
                        );
                    }
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Kafka 消息序列化失败",
                    e
            );
        }
    }
}