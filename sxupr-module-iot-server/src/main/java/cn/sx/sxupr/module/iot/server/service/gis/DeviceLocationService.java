package cn.sx.sxupr.module.iot.server.service.gis;

import cn.sx.sxupr.module.iot.server.controller.vo.gis.NearbyDeviceVO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.device.DeviceDO;
import cn.sx.sxupr.module.iot.server.dal.mapper.device.DeviceMapper;
import cn.sx.sxupr.module.iot.server.infra.postgis.DeviceLocationStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceLocationService {

    private final DeviceMapper deviceMapper;

    private final DeviceLocationStore locationStore;

    public DeviceLocationService(
            DeviceMapper deviceMapper,
            DeviceLocationStore locationStore) {

        this.deviceMapper = deviceMapper;
        this.locationStore = locationStore;
    }

    public void updateLocation(
            Long deviceId,
            double longitude,
            double latitude) {

        DeviceDO device =
                deviceMapper.selectById(
                        deviceId
                );

        if (device == null) {
            throw new IllegalArgumentException(
                    "设备不存在: " + deviceId
            );
        }

        locationStore.upsert(
                device.getId(),
                device.getProductId(),
                device.getDeviceName(),
                longitude,
                latitude
        );
    }

    public List<NearbyDeviceVO> queryNearby(
            double longitude,
            double latitude,
            double radiusMeters) {

        if (radiusMeters <= 0
                || radiusMeters > 100000) {

            throw new IllegalArgumentException(
                    "radiusMeters 必须位于 0~100000 之间"
            );
        }

        return locationStore.queryNearby(
                longitude,
                latitude,
                radiusMeters
        );
    }
}