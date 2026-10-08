package cn.sx.sxupr.module.iot.server.service.alarm;

import cn.sx.sxupr.module.iot.core.message.IotDevicePropertyMessage;
import cn.sx.sxupr.module.iot.server.config.IotMonitorProperties;
import cn.sx.sxupr.module.iot.server.dal.dataobject.device.DeviceDO;
import cn.sx.sxupr.module.iot.server.infra.redis.AlarmCounterStore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AlarmEvaluationService {

    private final AlarmCounterStore counterStore;

    private final AlarmService alarmService;

    private final IotMonitorProperties properties;

    public AlarmEvaluationService(
            AlarmCounterStore counterStore,
            AlarmService alarmService,
            IotMonitorProperties properties) {

        this.counterStore = counterStore;
        this.alarmService = alarmService;
        this.properties = properties;
    }

    public void evaluate(
            DeviceDO device,
            IotDevicePropertyMessage message,
            LocalDateTime serverReceiveTime) {

        Object rawWaterLevel =
                message.properties()
                        .get("waterLevel");

        if (rawWaterLevel == null) {
            return;
        }

        BigDecimal waterLevel =
                new BigDecimal(
                        rawWaterLevel.toString()
                );

        BigDecimal threshold =
                properties
                        .highWaterThreshold();

        if (waterLevel.compareTo(threshold) > 0) {

            long count =
                    counterStore
                            .incrementHighWater(
                                    device.getId()
                            );

            System.out.println(
                    "[ALARM-RULE] device="
                            + device.getDeviceName()
                            + ", waterLevel="
                            + waterLevel
                            + ", consecutive="
                            + count
            );

            if (count
                    >= properties
                    .highWaterConsecutiveCount()) {

                alarmService.triggerHighWater(
                        device.getId(),
                        waterLevel,
                        threshold,
                        serverReceiveTime
                );
            }

            return;
        }

        counterStore.resetHighWater(
                device.getId()
        );

        alarmService.recoverHighWater(
                device.getId(),
                serverReceiveTime
        );
    }
}