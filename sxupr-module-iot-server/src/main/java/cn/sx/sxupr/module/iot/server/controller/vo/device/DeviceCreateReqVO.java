package cn.sx.sxupr.module.iot.server.controller.vo.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DeviceCreateReqVO(

        @NotNull
        Long productId,

        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9_-]{1,64}$")
        String deviceName

) {
}