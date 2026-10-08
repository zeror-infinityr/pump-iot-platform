package cn.sx.sxupr.module.iot.server.controller.vo.thingmodel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ThingModelVersionCreateReqVO(

        String description,

        @NotEmpty
        List<@Valid ThingModelPropertyCreateReqVO> properties

) {
}