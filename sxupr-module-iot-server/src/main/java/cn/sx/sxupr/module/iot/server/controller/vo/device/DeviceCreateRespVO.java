package cn.sx.sxupr.module.iot.server.controller.vo.device;

public record DeviceCreateRespVO(

        DeviceVO device,

        String deviceSecret

) {
}