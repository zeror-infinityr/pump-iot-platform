package cn.sx.sxupr.module.iot.server.controller.device;

import cn.sx.sxupr.module.iot.server.controller.vo.device.*;
import cn.sx.sxupr.module.iot.server.service.device.DevicePropertyReportService;
import cn.sx.sxupr.module.iot.server.service.device.DeviceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/iot/devices")
public class DeviceController {

    private final DeviceService deviceService;
    private final DevicePropertyReportService propertyReportService;

    public DeviceController(
            DeviceService deviceService,
            DevicePropertyReportService propertyReportService) {

        this.deviceService = deviceService;
        this.propertyReportService = propertyReportService;
    }

    @GetMapping("/{id}/history")
    public List<DevicePropertyHistoryPointVO> history(
            @PathVariable Long id,
            @RequestParam String identifier,
            @RequestParam(defaultValue = "100")
            int limit) {

        return propertyReportService.getHistory(
                id,
                identifier,
                limit
        );
    }

    @PostMapping
    public DeviceCreateRespVO create(
            @Valid @RequestBody DeviceCreateReqVO reqVO) {

        return deviceService.createDevice(reqVO);
    }

    @GetMapping
    public List<DeviceVO> list() {

        return deviceService.listDevices();
    }

    @GetMapping("/{id}")
    public DeviceVO get(
            @PathVariable Long id) {

        return deviceService.getDevice(id);
    }

    @PostMapping("/{id}/reset-secret")
    public DeviceSecretResetRespVO resetSecret(
            @PathVariable Long id) {

        return deviceService.resetSecret(id);
    }

    @GetMapping("/{id}/latest-properties")
    public DeviceLatestStateVO getLatestProperties(
            @PathVariable Long id) {

        return propertyReportService.getLatestState(id);
    }
}