package cn.sx.sxupr.module.iot.server.controller.product;

import java.time.LocalDateTime;

public record ProductRespVO(
        Long id,
        String productKey,
        String name,
        String description,
        Integer status,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}