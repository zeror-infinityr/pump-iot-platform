package cn.sx.sxupr.module.iot.server.controller.vo.thingmodel;

import cn.sx.sxupr.module.iot.core.enums.IotDataType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record ThingModelPropertyCreateReqVO(

        @NotBlank
        @Pattern(regexp = "^[a-z][A-Za-z0-9]{0,63}$")
        String identifier,

        @NotBlank
        String name,

        @NotNull
        IotDataType dataType,

        String unit,

        BigDecimal minValue,

        BigDecimal maxValue,

        Boolean required

) {
}