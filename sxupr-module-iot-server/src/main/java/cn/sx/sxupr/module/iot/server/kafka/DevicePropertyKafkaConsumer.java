package cn.sx.sxupr.module.iot.server.kafka;

import cn.sx.sxupr.module.iot.core.kafka.IotKafkaTopics;
import cn.sx.sxupr.module.iot.core.message.IotDevicePropertyMessage;
import cn.sx.sxupr.module.iot.server.service.device.DevicePropertyReportService;
import cn.sx.sxupr.module.iot.server.infra.redis.DeviceMessageIdempotencyStore;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class DevicePropertyKafkaConsumer {

    private final JsonMapper jsonMapper;

    private final DevicePropertyReportService reportService;

    private final DeviceMessageIdempotencyStore idempotencyStore;

    public DevicePropertyKafkaConsumer(
            JsonMapper jsonMapper,
            DevicePropertyReportService reportService,
            DeviceMessageIdempotencyStore idempotencyStore) {

        this.jsonMapper = jsonMapper;
        this.reportService = reportService;
        this.idempotencyStore = idempotencyStore;
    }

    @KafkaListener(
            topics = IotKafkaTopics.DEVICE_PROPERTY_UPSTREAM,
            groupId = "iot-server-property-group"
    )
    public void onMessage(String payload) {

        try {

            IotDevicePropertyMessage message =
                    jsonMapper.readValue(
                            payload,
                            IotDevicePropertyMessage.class
                    );

            validateMessage(message);

            boolean processed =
                    idempotencyStore.isProcessed(
                            message.productKey(),
                            message.deviceName(),
                            message.requestId()
                    );

            if (processed) {

                System.out.println(
                        "[SERVER-KAFKA] duplicate skipped requestId="
                                + message.requestId()
                );

                return;
            }

            System.out.println(
                    "[SERVER-KAFKA] received requestId="
                            + message.requestId()
                            + ", device="
                            + message.deviceName()
            );

            reportService.report(message);

            idempotencyStore.markProcessed(
                    message.productKey(),
                    message.deviceName(),
                    message.requestId()
            );

            System.out.println(
                    "[SERVER-KAFKA] processed requestId="
                            + message.requestId()
            );

        } catch (Exception e) {

            System.err.println(
                    "[SERVER-KAFKA] process failed: "
                            + e.getMessage()
            );

            throw new IllegalStateException(
                    "Kafka IoT 消息处理失败",
                    e
            );
        }
    }

    private void validateMessage(
            IotDevicePropertyMessage message) {

        if (message.requestId() == null
                || message.requestId().isBlank()) {

            throw new IllegalArgumentException(
                    "requestId 不能为空"
            );
        }

        if (message.productKey() == null
                || message.productKey().isBlank()) {

            throw new IllegalArgumentException(
                    "productKey 不能为空"
            );
        }

        if (message.deviceName() == null
                || message.deviceName().isBlank()) {

            throw new IllegalArgumentException(
                    "deviceName 不能为空"
            );
        }

        if (message.properties() == null
                || message.properties().isEmpty()) {

            throw new IllegalArgumentException(
                    "properties 不能为空"
            );
        }
    }
}