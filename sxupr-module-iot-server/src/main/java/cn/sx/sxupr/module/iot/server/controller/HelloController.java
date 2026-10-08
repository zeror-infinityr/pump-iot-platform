package cn.sx.sxupr.module.iot.server.controller;

import cn.sx.sxupr.module.iot.core.enums.IotDeviceStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/iot")
public class HelloController {

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        return Map.of(
                "message", "Pump IoT Platform is running",
                "deviceStatus", IotDeviceStatus.ONLINE,
                "time", LocalDateTime.now().toString()
        );
    }

}