package cn.sx.sxupr.module.iot.server.controller.gis;

import cn.sx.sxupr.module.iot.server.controller.vo.gis.DeviceLocationUpdateReqVO;
import cn.sx.sxupr.module.iot.server.controller.vo.gis.NearbyDeviceVO;
import cn.sx.sxupr.module.iot.server.service.gis.DeviceLocationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/iot/gis")
public class DeviceLocationController {

    private final DeviceLocationService locationService;

    public DeviceLocationController(
            DeviceLocationService locationService) {

        this.locationService =
                locationService;
    }

    @PutMapping(
            "/devices/{deviceId}/location"
    )
    public void updateLocation(
            @PathVariable Long deviceId,
            @Valid @RequestBody
            DeviceLocationUpdateReqVO reqVO) {

        locationService.updateLocation(
                deviceId,
                reqVO.longitude(),
                reqVO.latitude()
        );
    }

    @GetMapping("/devices/nearby")
    public List<NearbyDeviceVO> nearby(

            @RequestParam double longitude,

            @RequestParam double latitude,

            @RequestParam(
                    defaultValue = "5000"
            )
            double radiusMeters) {

        return locationService.queryNearby(
                longitude,
                latitude,
                radiusMeters
        );
    }
}