package cn.sx.sxupr.module.iot.server.controller.vo.device;

public record DevicePropertyHistoryPointVO(

        Long deviceTimestamp,

        Long serverReceiveTimestamp,

        String requestId,

        String identifier,

        String dataType,

        Object value

) {
}