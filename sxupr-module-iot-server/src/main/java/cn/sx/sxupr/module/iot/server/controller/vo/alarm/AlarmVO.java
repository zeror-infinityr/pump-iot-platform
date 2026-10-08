package cn.sx.sxupr.module.iot.server.controller.vo.alarm;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AlarmVO(

        Long id,

        Long deviceId,

        String alarmType,

        String alarmLevel,

        String status,

        BigDecimal triggerValue,

        BigDecimal thresholdValue,

        LocalDateTime firstTriggerTime,

        LocalDateTime lastTriggerTime,

        LocalDateTime recoverTime

) {
}