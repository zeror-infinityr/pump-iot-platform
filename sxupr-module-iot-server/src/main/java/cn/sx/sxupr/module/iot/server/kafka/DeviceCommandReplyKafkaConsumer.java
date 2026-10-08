package cn.sx.sxupr.module.iot.server.kafka;

import cn.sx.sxupr.module.iot.core.kafka.IotKafkaTopics;
import cn.sx.sxupr.module.iot.core.message.IotDeviceCommandReplyMessage;
import cn.sx.sxupr.module.iot.server.service.command.DeviceCommandService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class DeviceCommandReplyKafkaConsumer {

    private final JsonMapper jsonMapper;

    private final DeviceCommandService commandService;

    public DeviceCommandReplyKafkaConsumer(
            JsonMapper jsonMapper,
            DeviceCommandService commandService) {

        this.jsonMapper = jsonMapper;
        this.commandService = commandService;
    }

    @KafkaListener(
            topics =
                    IotKafkaTopics
                            .DEVICE_COMMAND_REPLY_UPSTREAM,
            groupId =
                    "iot-server-command-reply-group"
    )
    public void onReply(
            String payload) {

        try {

            IotDeviceCommandReplyMessage reply =
                    jsonMapper.readValue(
                            payload,
                            IotDeviceCommandReplyMessage.class
                    );

            commandService.handleReply(
                    reply
            );

            System.out.println(
                    "[SERVER-CMD] reply processed"
                            + ", requestId="
                            + reply.requestId()
                            + ", success="
                            + reply.success()
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "设备命令回执处理失败",
                    e
            );
        }
    }
}