package cn.sx.sxupr.module.iot.core.topic;

public final class IotTopics {

    private IotTopics() {
    }

    public static String propertyPost(
            String productKey,
            String deviceName) {

        return "iot/"
                + productKey
                + "/"
                + deviceName
                + "/property/post";
    }

    public static String propertyPostFilter() {

        return "iot/+/+/property/post";
    }

    public static IotDeviceIdentity parsePropertyPost(
            String topic) {

        String[] parts = topic.split("/");

        if (parts.length != 5
                || !"iot".equals(parts[0])
                || !"property".equals(parts[3])
                || !"post".equals(parts[4])) {

            throw new IllegalArgumentException(
                    "非法属性上报 Topic: " + topic
            );
        }

        return new IotDeviceIdentity(
                parts[1],
                parts[2]
        );
    }

    public static String commandDown(
            String productKey,
            String deviceName) {

        return "iot/"
                + productKey
                + "/"
                + deviceName
                + "/command/down";
    }

    public static String commandReply(
            String productKey,
            String deviceName) {

        return "iot/"
                + productKey
                + "/"
                + deviceName
                + "/command/reply";
    }

    public static String commandReplyFilter() {

        return "iot/+/+/command/reply";
    }

    public static IotDeviceIdentity parseCommandReply(
            String topic) {

        String[] parts = topic.split("/");

        if (parts.length != 5
                || !"iot".equals(parts[0])
                || !"command".equals(parts[3])
                || !"reply".equals(parts[4])) {

            throw new IllegalArgumentException(
                    "非法命令回执 Topic: " + topic
            );
        }

        return new IotDeviceIdentity(
                parts[1],
                parts[2]
        );
    }
}