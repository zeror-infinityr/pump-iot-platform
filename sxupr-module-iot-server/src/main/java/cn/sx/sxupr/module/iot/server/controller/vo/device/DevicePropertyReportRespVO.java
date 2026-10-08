package cn.sx.sxupr.module.iot.server.controller.vo.device;

import java.time.LocalDateTime;
import java.util.Map;

public record DevicePropertyReportRespVO(

        String requestId,

        Long deviceId,

        String deviceName,

        Long deviceTimestamp,

        LocalDateTime serverReceiveTime,

        String status,

        Map<String, Object> properties

) {
}