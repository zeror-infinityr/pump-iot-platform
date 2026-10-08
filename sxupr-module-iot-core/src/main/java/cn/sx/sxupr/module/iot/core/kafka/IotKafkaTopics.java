package cn.sx.sxupr.module.iot.core.kafka;

public final class IotKafkaTopics {

    private IotKafkaTopics() {
    }

    public static final String DEVICE_PROPERTY_UPSTREAM =
            "iot-device-property-upstream";

    public static final String DEVICE_COMMAND_DOWNSTREAM =
            "iot-device-command-downstream";

    public static final String DEVICE_COMMAND_REPLY_UPSTREAM =
            "iot-device-command-reply-upstream";
}