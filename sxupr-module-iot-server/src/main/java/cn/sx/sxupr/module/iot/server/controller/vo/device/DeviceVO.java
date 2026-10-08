package cn.sx.sxupr.module.iot.server.controller.vo.device;

import java.time.LocalDateTime;

public record DeviceVO(

        Long id,

        Long productId,

        String productKey,

        String deviceKey,

        String deviceName,

        Integer thingModelVersion,

        String onlineStatus,

        LocalDateTime lastReportTime,

        LocalDateTime createTime,

        LocalDateTime updateTime

) {
}