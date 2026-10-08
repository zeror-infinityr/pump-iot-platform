package cn.sx.sxupr.module.iot.gateway.dto;

import java.util.Map;

public record DevicePropertyForwardRequest(

        String requestId,

        String productKey,

        String deviceName,

        Long timestamp,

        Map<String, Object> properties

) {
}