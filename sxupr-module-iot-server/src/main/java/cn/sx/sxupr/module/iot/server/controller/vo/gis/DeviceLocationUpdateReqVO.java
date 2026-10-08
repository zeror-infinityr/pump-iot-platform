package cn.sx.sxupr.module.iot.server.controller.vo.gis;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record DeviceLocationUpdateReqVO(

        @NotNull
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        Double longitude,

        @NotNull
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        Double latitude

) {
}