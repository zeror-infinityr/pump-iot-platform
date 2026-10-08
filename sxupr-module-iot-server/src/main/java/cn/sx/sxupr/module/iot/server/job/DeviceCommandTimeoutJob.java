package cn.sx.sxupr.module.iot.server.job;

import cn.sx.sxupr.module.iot.core.enums.IotDeviceCommandStatus;
import cn.sx.sxupr.module.iot.server.config.IotCommandProperties;
import cn.sx.sxupr.module.iot.server.dal.dataobject.command.DeviceCommandDO;
import cn.sx.sxupr.module.iot.server.dal.mapper.command.DeviceCommandMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DeviceCommandTimeoutJob {

    private final DeviceCommandMapper commandMapper;

    private final IotCommandProperties properties;

    public DeviceCommandTimeoutJob(
            DeviceCommandMapper commandMapper,
            IotCommandProperties properties) {

        this.commandMapper = commandMapper;
        this.properties = properties;
    }

    @Scheduled(fixedDelay = 5000)
    public void checkTimeout() {

        LocalDateTime before =
                LocalDateTime.now()
                        .minusSeconds(
                                properties.timeoutSeconds()
                        );

        List<DeviceCommandDO> commands =
                commandMapper.selectList(
                        Wrappers
                                .<DeviceCommandDO>lambdaQuery()
                                .eq(
                                        DeviceCommandDO::getStatus,
                                        IotDeviceCommandStatus
                                                .SENT
                                                .name()
                                )
                                .lt(
                                        DeviceCommandDO::getSentTime,
                                        before
                                )
                );

        for (DeviceCommandDO command : commands) {

            commandMapper.update(
                    null,
                    Wrappers
                            .<DeviceCommandDO>lambdaUpdate()
                            .eq(
                                    DeviceCommandDO::getId,
                                    command.getId()
                            )
                            .eq(
                                    DeviceCommandDO::getStatus,
                                    IotDeviceCommandStatus
                                            .SENT
                                            .name()
                            )
                            .set(
                                    DeviceCommandDO::getStatus,
                                    IotDeviceCommandStatus
                                            .TIMEOUT
                                            .name()
                            )
            );
        }
    }
}