package cn.sx.sxupr.module.iot.server.controller.vo.device;

import java.util.Map;

public record DeviceLatestStateVO(

        Long deviceId,

        String productKey,

        String deviceName,

        Integer thingModelVersion,

        String requestId,

        Long deviceTimestamp,

        Long serverReceiveTimestamp,

        Map<String, Object> properties

) {
}