package cn.sx.sxupr.module.iot.gateway.kafka;

import cn.sx.sxupr.module.iot.core.kafka.IotKafkaTopics;
import cn.sx.sxupr.module.iot.core.message.IotDeviceCommandMessage;
import cn.sx.sxupr.module.iot.core.topic.IotTopics;
import cn.sx.sxupr.module.iot.gateway.mqtt.IotMqttClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class DeviceCommandKafkaConsumer {

    private final JsonMapper jsonMapper;

    private final IotMqttClient mqttClient;

    public DeviceCommandKafkaConsumer(
            JsonMapper jsonMapper,
            IotMqttClient mqttClient) {

        this.jsonMapper = jsonMapper;
        this.mqttClient = mqttClient;
    }

    @KafkaListener(
            topics =
                    IotKafkaTopics
                            .DEVICE_COMMAND_DOWNSTREAM,
            groupId =
                    "iot-gateway-command-group"
    )
    public void onCommand(
            String payload) {

        try {

            IotDeviceCommandMessage message =
                    jsonMapper.readValue(
                            payload,
                            IotDeviceCommandMessage.class
                    );

            String topic =
                    IotTopics.commandDown(
                            message.productKey(),
                            message.deviceName()
                    );

            mqttClient.publish(
                    topic,
                    payload,
                    1
            );

            System.out.println(
                    "[GATEWAY-CMD] MQTT command sent"
                            + ", requestId="
                            + message.requestId()
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Gateway 下发设备命令失败",
                    e
            );
        }
    }
}