package cn.sx.sxupr.module.iot.server.controller.alarm;

import cn.sx.sxupr.module.iot.server.controller.vo.alarm.AlarmVO;
import cn.sx.sxupr.module.iot.server.service.alarm.AlarmService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/iot/devices")
public class AlarmController {

    private final AlarmService alarmService;

    public AlarmController(
            AlarmService alarmService) {

        this.alarmService = alarmService;
    }

    @GetMapping("/{deviceId}/alarms")
    public List<AlarmVO> list(
            @PathVariable Long deviceId) {

        return alarmService
                .listVOByDevice(deviceId);
    }
}