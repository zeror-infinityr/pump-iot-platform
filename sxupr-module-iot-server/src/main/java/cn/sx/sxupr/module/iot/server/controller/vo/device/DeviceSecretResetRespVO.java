package cn.sx.sxupr.module.iot.server.controller.vo.device;

public record DeviceSecretResetRespVO(

        Long deviceId,

        String deviceKey,

        String deviceSecret

) {
}