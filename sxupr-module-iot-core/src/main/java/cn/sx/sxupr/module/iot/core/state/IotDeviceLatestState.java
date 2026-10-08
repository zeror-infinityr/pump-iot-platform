package cn.sx.sxupr.module.iot.core.state;

import java.util.Map;

/**
 * IoT 设备最新属性状态。
 *
 * Redis 中每台设备只保存最新的一份状态。
 */
public record IotDeviceLatestState(

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