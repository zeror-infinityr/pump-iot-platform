package cn.sx.sxupr.module.iot.core.message;

import java.util.Map;

public record IotDeviceCommandMessage(

        String requestId,

        String productKey,

        String deviceName,

        String method,

        Long serverTimestamp,

        Map<String, Object> params

) {
}