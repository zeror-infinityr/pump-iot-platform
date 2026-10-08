package cn.sx.sxupr.module.iot.server.controller.device;


import cn.sx.sxupr.module.iot.server.controller.vo.device.DevicePropertyReportReqVO;
import cn.sx.sxupr.module.iot.server.controller.vo.device.DevicePropertyReportRespVO;
import cn.sx.sxupr.module.iot.server.service.device.DevicePropertyReportService;


import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/iot/device")
public class DevicePropertyReportController {

    private final DevicePropertyReportService reportService;

    public DevicePropertyReportController(
            DevicePropertyReportService reportService) {

        this.reportService = reportService;
    }

    @PostMapping("/property/post")
    public DevicePropertyReportRespVO report(
            @Valid
            @RequestBody
            DevicePropertyReportReqVO reqVO) {

        return reportService.report(reqVO);
    }
}