package cn.sx.sxupr.module.iot.server.controller.vo.command;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SetSamplingIntervalReqVO(

        @NotNull
        @Min(1)
        @Max(3600)
        Integer intervalSeconds

) {
}