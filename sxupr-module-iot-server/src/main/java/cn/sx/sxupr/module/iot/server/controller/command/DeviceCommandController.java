package cn.sx.sxupr.module.iot.server.controller.command;

import cn.sx.sxupr.module.iot.server.controller.vo.command.DeviceCommandVO;
import cn.sx.sxupr.module.iot.server.controller.vo.command.SetSamplingIntervalReqVO;
import cn.sx.sxupr.module.iot.server.service.command.DeviceCommandService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/iot/devices")
public class DeviceCommandController {

    private final DeviceCommandService commandService;

    public DeviceCommandController(
            DeviceCommandService commandService) {

        this.commandService = commandService;
    }

    @PostMapping(
            "/{deviceId}/commands/sampling-interval"
    )
    public DeviceCommandVO setSamplingInterval(
            @PathVariable Long deviceId,
            @Valid @RequestBody
            SetSamplingIntervalReqVO reqVO) {

        return commandService
                .setSamplingInterval(
                        deviceId,
                        reqVO.intervalSeconds()
                );
    }

    @GetMapping("/{deviceId}/commands")
    public List<DeviceCommandVO> list(
            @PathVariable Long deviceId) {

        return commandService
                .listByDevice(deviceId);
    }
}