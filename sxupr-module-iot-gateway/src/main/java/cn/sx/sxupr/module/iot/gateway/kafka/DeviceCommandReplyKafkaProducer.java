package cn.sx.sxupr.module.iot.gateway.kafka;

import cn.sx.sxupr.module.iot.core.kafka.IotKafkaTopics;
import cn.sx.sxupr.module.iot.core.message.IotDeviceCommandReplyMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class DeviceCommandReplyKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final JsonMapper jsonMapper;

    public DeviceCommandReplyKafkaProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            JsonMapper jsonMapper) {

        this.kafkaTemplate = kafkaTemplate;
        this.jsonMapper = jsonMapper;
    }

    public void send(
            IotDeviceCommandReplyMessage reply) {

        try {

            String payload =
                    jsonMapper.writeValueAsString(
                            reply
                    );

            String key =
                    reply.productKey()
                            + ":"
                            + reply.deviceName();

            kafkaTemplate.send(
                    IotKafkaTopics
                            .DEVICE_COMMAND_REPLY_UPSTREAM,
                    key,
                    payload
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "命令回执发送 Kafka 失败",
                    e
            );
        }
    }
}