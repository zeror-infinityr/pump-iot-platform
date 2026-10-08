package cn.sx.sxupr.module.iot.server.controller.vo.thingmodel;

import java.math.BigDecimal;

public record ThingModelPropertyVO(

        Long id,

        String identifier,

        String name,

        String dataType,

        String unit,

        BigDecimal minValue,

        BigDecimal maxValue,

        Boolean required

) {
}