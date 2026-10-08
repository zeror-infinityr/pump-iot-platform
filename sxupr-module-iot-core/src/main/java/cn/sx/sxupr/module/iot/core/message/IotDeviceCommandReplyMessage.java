package cn.sx.sxupr.module.iot.core.message;

import java.util.Map;

public record IotDeviceCommandReplyMessage(

        String requestId,

        String productKey,

        String deviceName,

        boolean success,

        String message,

        Long deviceTimestamp,

        Map<String, Object> result

) {
}