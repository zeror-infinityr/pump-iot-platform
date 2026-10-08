package cn.sx.sxupr.simulator.command;

import cn.sx.sxupr.module.iot.core.message.IotDeviceCommandMessage;
import cn.sx.sxupr.module.iot.core.message.IotDeviceCommandReplyMessage;
import cn.sx.sxupr.module.iot.core.topic.IotTopics;
import cn.sx.sxupr.simulator.config.SimulatorProperties;
import cn.sx.sxupr.simulator.device.SamplingIntervalState;
import cn.sx.sxupr.simulator.mqtt.SimulatorMqttClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class DeviceCommandHandler
        implements ApplicationRunner {

    private final SimulatorProperties properties;
    private final SimulatorMqttClient mqttClient;
    private final SamplingIntervalState intervalState;
    private final JsonMapper jsonMapper;

    public DeviceCommandHandler(
            SimulatorProperties properties,
            SimulatorMqttClient mqttClient,
            SamplingIntervalState intervalState,
            JsonMapper jsonMapper) {

        this.properties = properties;
        this.mqttClient = mqttClient;
        this.intervalState = intervalState;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void run(
            ApplicationArguments args)
            throws Exception {

        String topic =
                IotTopics.commandDown(
                        properties.productKey(),
                        properties.deviceName()
                );

        mqttClient.subscribe(
                topic,
                this::handleCommand
        );

        System.out.println(
                "[DEVICE] subscribed command topic="
                        + topic
        );
    }

    private void handleCommand(
            String topic,
            org.eclipse.paho.mqttv5.common.MqttMessage mqttMessage) {

        try {

            String payload =
                    new String(
                            mqttMessage.getPayload(),
                            StandardCharsets.UTF_8
                    );

            IotDeviceCommandMessage command =
                    jsonMapper.readValue(
                            payload,
                            IotDeviceCommandMessage.class
                    );

            IotDeviceCommandReplyMessage reply =
                    execute(command);

            String replyTopic =
                    IotTopics.commandReply(
                            properties.productKey(),
                            properties.deviceName()
                    );

            mqttClient.publish(
                    replyTopic,
                    jsonMapper.writeValueAsString(
                            reply
                    )
            );

        } catch (Exception e) {

            System.err.println(
                    "[DEVICE] command failed: "
                            + e.getMessage()
            );
        }
    }

    private IotDeviceCommandReplyMessage execute(
            IotDeviceCommandMessage command) {

        if (!"SET_SAMPLING_INTERVAL"
                .equals(command.method())) {

            return failure(
                    command,
                    "unsupported method: "
                            + command.method()
            );
        }

        Object raw =
                command.params()
                        .get("intervalSeconds");

        if (!(raw instanceof Number number)) {

            return failure(
                    command,
                    "intervalSeconds invalid"
            );
        }

        long seconds =
                number.longValue();

        if (seconds < 1
                || seconds > 3600) {

            return failure(
                    command,
                    "intervalSeconds out of range"
            );
        }

        intervalState.setIntervalSeconds(
                seconds
        );

        System.out.println(
                "[DEVICE] sampling interval changed to "
                        + seconds
                        + " seconds"
        );

        return new IotDeviceCommandReplyMessage(
                command.requestId(),
                properties.productKey(),
                properties.deviceName(),
                true,
                "sampling interval updated",
                System.currentTimeMillis(),
                Map.of(
                        "intervalSeconds",
                        seconds
                )
        );
    }

    private IotDeviceCommandReplyMessage failure(
            IotDeviceCommandMessage command,
            String reason) {

        return new IotDeviceCommandReplyMessage(
                command.requestId(),
                properties.productKey(),
                properties.deviceName(),
                false,
                reason,
                System.currentTimeMillis(),
                Map.of()
        );
    }
}