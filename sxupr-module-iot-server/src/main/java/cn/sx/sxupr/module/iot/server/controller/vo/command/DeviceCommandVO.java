package cn.sx.sxupr.module.iot.server.controller.vo.command;

import java.time.LocalDateTime;

public record DeviceCommandVO(

        Long id,

        Long deviceId,

        String requestId,

        String commandType,

        String commandParams,

        String status,

        String replyMessage,

        LocalDateTime createTime,

        LocalDateTime sentTime,

        LocalDateTime replyTime

) {
}