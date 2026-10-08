package cn.sx.sxupr.module.iot.server.controller.vo.thingmodel;

import java.time.LocalDateTime;
import java.util.List;

public record ThingModelVersionVO(

        Long id,

        Long productId,

        Integer version,

        String description,

        LocalDateTime createTime,

        LocalDateTime updateTime,

        List<ThingModelPropertyVO> properties

) {
}