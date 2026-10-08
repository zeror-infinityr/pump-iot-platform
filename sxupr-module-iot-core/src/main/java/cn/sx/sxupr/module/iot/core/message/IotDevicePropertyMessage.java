package cn.sx.sxupr.module.iot.core.message;

import java.util.Map;

/**
 * IoT 设备属性上报的统一消息模型。
 *
 * 将来无论消息来自 HTTP、MQTT，
 * 在进入业务处理阶段后都统一转换成该结构。
 */
public record IotDevicePropertyMessage(

        String requestId,

        String productKey,

        String deviceName,

        Long deviceTimestamp,

        Map<String, Object> properties

) {
}