package cn.sx.sxupr.module.iot.server.job;

import cn.sx.sxupr.module.iot.core.enums.IotDeviceStatus;
import cn.sx.sxupr.module.iot.server.config.IotMonitorProperties;
import cn.sx.sxupr.module.iot.server.dal.dataobject.device.DeviceDO;
import cn.sx.sxupr.module.iot.server.dal.mapper.device.DeviceMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DeviceOnlineStatusJob {

    private final DeviceMapper deviceMapper;

    private final IotMonitorProperties monitorProperties;

    public DeviceOnlineStatusJob(
            DeviceMapper deviceMapper,
            IotMonitorProperties monitorProperties) {

        this.deviceMapper = deviceMapper;
        this.monitorProperties = monitorProperties;
    }

    @Scheduled(fixedDelay = 5000)
    public void checkOfflineDevices() {

        LocalDateTime offlineBefore =
                LocalDateTime.now()
                        .minusSeconds(
                                monitorProperties
                                        .offlineTimeoutSeconds()
                        );

        List<DeviceDO> timeoutDevices =
                deviceMapper.selectList(
                        Wrappers
                                .<DeviceDO>lambdaQuery()
                                .eq(
                                        DeviceDO::getOnlineStatus,
                                        IotDeviceStatus.ONLINE.name()
                                )
                                .lt(
                                        DeviceDO::getLastReportTime,
                                        offlineBefore
                                )
                );

        for (DeviceDO device : timeoutDevices) {

            int affected =
                    deviceMapper.update(
                            null,

                            Wrappers
                                    .<DeviceDO>lambdaUpdate()
                                    .eq(
                                            DeviceDO::getId,
                                            device.getId()
                                    )
                                    .eq(
                                            DeviceDO::getOnlineStatus,
                                            IotDeviceStatus.ONLINE.name()
                                    )
                                    .lt(
                                            DeviceDO::getLastReportTime,
                                            offlineBefore
                                    )
                                    .set(
                                            DeviceDO::getOnlineStatus,
                                            IotDeviceStatus.OFFLINE.name()
                                    )
                    );

            if (affected > 0) {

                System.out.println(
                        "[DEVICE-OFFLINE] deviceId="
                                + device.getId()
                                + ", deviceName="
                                + device.getDeviceName()
                );
            }
        }
    }
}