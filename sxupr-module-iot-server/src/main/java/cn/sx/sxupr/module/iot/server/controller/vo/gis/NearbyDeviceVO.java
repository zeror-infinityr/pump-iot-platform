package cn.sx.sxupr.module.iot.server.controller.vo.gis;

public record NearbyDeviceVO(

        Long deviceId,

        Long productId,

        String deviceName,

        Double longitude,

        Double latitude,

        Double distanceMeters

) {
}