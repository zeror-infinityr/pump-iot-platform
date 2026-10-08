package cn.sx.sxupr.module.iot.server.controller.vo.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record DevicePropertyReportReqVO(

        @NotBlank
        String requestId,

        @NotBlank
        String productKey,

        @NotBlank
        String deviceName,

        @NotNull
        Long timestamp,

        @NotEmpty
        Map<String, Object> properties

) {
}