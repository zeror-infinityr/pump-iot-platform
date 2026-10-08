package cn.sx.sxupr.module.iot.server.service.alarm;

import cn.sx.sxupr.module.iot.core.enums.IotAlarmStatus;
import cn.sx.sxupr.module.iot.core.enums.IotAlarmType;
import cn.sx.sxupr.module.iot.server.controller.vo.alarm.AlarmVO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.alarm.AlarmDO;
import cn.sx.sxupr.module.iot.server.dal.mapper.alarm.AlarmMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlarmService {

    private final AlarmMapper alarmMapper;

    public AlarmService(
            AlarmMapper alarmMapper) {

        this.alarmMapper = alarmMapper;
    }

    public void triggerHighWater(
            Long deviceId,
            BigDecimal currentValue,
            BigDecimal threshold,
            LocalDateTime triggerTime) {

        AlarmDO activeAlarm =
                findActiveHighWater(deviceId);

        if (activeAlarm == null) {

            AlarmDO alarm =
                    new AlarmDO();

            alarm.setDeviceId(deviceId);

            alarm.setAlarmType(
                    IotAlarmType
                            .HIGH_WATER
                            .name()
            );

            alarm.setAlarmLevel(
                    "WARNING"
            );

            alarm.setStatus(
                    IotAlarmStatus
                            .ACTIVE
                            .name()
            );

            alarm.setTriggerValue(
                    currentValue
            );

            alarm.setThresholdValue(
                    threshold
            );

            alarm.setFirstTriggerTime(
                    triggerTime
            );

            alarm.setLastTriggerTime(
                    triggerTime
            );

            alarmMapper.insert(alarm);

            System.out.println(
                    "[ALARM] HIGH_WATER created"
                            + ", deviceId="
                            + deviceId
                            + ", value="
                            + currentValue
            );

            return;
        }

        activeAlarm.setTriggerValue(
                currentValue
        );

        activeAlarm.setLastTriggerTime(
                triggerTime
        );

        alarmMapper.updateById(
                activeAlarm
        );
    }

    public void recoverHighWater(
            Long deviceId,
            LocalDateTime recoverTime) {

        AlarmDO activeAlarm =
                findActiveHighWater(deviceId);

        if (activeAlarm == null) {
            return;
        }

        activeAlarm.setStatus(
                IotAlarmStatus
                        .RECOVERED
                        .name()
        );

        activeAlarm.setRecoverTime(
                recoverTime
        );

        alarmMapper.updateById(
                activeAlarm
        );

        System.out.println(
                "[ALARM] HIGH_WATER recovered"
                        + ", deviceId="
                        + deviceId
        );
    }

    public AlarmDO findActiveHighWater(
            Long deviceId) {

        return alarmMapper.selectOne(
                Wrappers
                        .<AlarmDO>lambdaQuery()
                        .eq(
                                AlarmDO::getDeviceId,
                                deviceId
                        )
                        .eq(
                                AlarmDO::getAlarmType,
                                IotAlarmType
                                        .HIGH_WATER
                                        .name()
                        )
                        .eq(
                                AlarmDO::getStatus,
                                IotAlarmStatus
                                        .ACTIVE
                                        .name()
                        )
                        .last("LIMIT 1")
        );
    }

    public List<AlarmDO> listByDevice(
            Long deviceId) {

        return alarmMapper.selectList(
                Wrappers
                        .<AlarmDO>lambdaQuery()
                        .eq(
                                AlarmDO::getDeviceId,
                                deviceId
                        )
                        .orderByDesc(
                                AlarmDO::getId
                        )
        );
    }

    public List<AlarmVO> listVOByDevice(
            Long deviceId) {

        return listByDevice(deviceId)
                .stream()
                .map(
                        alarm ->
                                new AlarmVO(
                                        alarm.getId(),
                                        alarm.getDeviceId(),
                                        alarm.getAlarmType(),
                                        alarm.getAlarmLevel(),
                                        alarm.getStatus(),
                                        alarm.getTriggerValue(),
                                        alarm.getThresholdValue(),
                                        alarm.getFirstTriggerTime(),
                                        alarm.getLastTriggerTime(),
                                        alarm.getRecoverTime()
                                )
                )
                .toList();
    }
}