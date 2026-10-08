package cn.sx.sxupr.module.iot.server.service.command;

import cn.sx.sxupr.module.iot.core.enums.IotDeviceCommandStatus;
import cn.sx.sxupr.module.iot.core.message.IotDeviceCommandMessage;
import cn.sx.sxupr.module.iot.core.message.IotDeviceCommandReplyMessage;
import cn.sx.sxupr.module.iot.server.controller.vo.command.DeviceCommandVO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.command.DeviceCommandDO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.device.DeviceDO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.product.ProductDO;
import cn.sx.sxupr.module.iot.server.dal.mapper.command.DeviceCommandMapper;
import cn.sx.sxupr.module.iot.server.dal.mapper.device.DeviceMapper;
import cn.sx.sxupr.module.iot.server.dal.mapper.product.ProductMapper;
import cn.sx.sxupr.module.iot.server.kafka.DeviceCommandKafkaProducer;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DeviceCommandService {

    private final DeviceCommandMapper commandMapper;
    private final DeviceMapper deviceMapper;
    private final ProductMapper productMapper;
    private final DeviceCommandKafkaProducer kafkaProducer;
    private final JsonMapper jsonMapper;

    public DeviceCommandService(
            DeviceCommandMapper commandMapper,
            DeviceMapper deviceMapper,
            ProductMapper productMapper,
            DeviceCommandKafkaProducer kafkaProducer,
            JsonMapper jsonMapper) {

        this.commandMapper = commandMapper;
        this.deviceMapper = deviceMapper;
        this.productMapper = productMapper;
        this.kafkaProducer = kafkaProducer;
        this.jsonMapper = jsonMapper;
    }

    public DeviceCommandVO setSamplingInterval(
            Long deviceId,
            Integer intervalSeconds) {

        DeviceDO device =
                deviceMapper.selectById(deviceId);

        if (device == null) {
            throw new IllegalArgumentException(
                    "设备不存在: " + deviceId
            );
        }

        ProductDO product =
                productMapper.selectById(
                        device.getProductId()
                );

        if (product == null) {
            throw new IllegalStateException(
                    "设备所属产品不存在"
            );
        }

        String requestId =
                UUID.randomUUID().toString();

        Map<String, Object> params =
                Map.of(
                        "intervalSeconds",
                        intervalSeconds
                );

        DeviceCommandDO command =
                new DeviceCommandDO();

        command.setDeviceId(deviceId);
        command.setRequestId(requestId);
        command.setCommandType(
                "SET_SAMPLING_INTERVAL"
        );

        try {
            command.setCommandParams(
                    jsonMapper.writeValueAsString(params)
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                    "命令参数序列化失败",
                    e
            );
        }

        command.setStatus(
                IotDeviceCommandStatus
                        .SUBMITTED
                        .name()
        );

        commandMapper.insert(command);

        IotDeviceCommandMessage message =
                new IotDeviceCommandMessage(
                        requestId,
                        product.getProductKey(),
                        device.getDeviceName(),
                        "SET_SAMPLING_INTERVAL",
                        System.currentTimeMillis(),
                        params
                );

        kafkaProducer.send(message);

        command.setStatus(
                IotDeviceCommandStatus
                        .SENT
                        .name()
        );

        command.setSentTime(
                LocalDateTime.now()
        );

        commandMapper.updateById(command);

        return toVO(command);
    }

    public void handleReply(
            IotDeviceCommandReplyMessage reply) {

        DeviceCommandDO command =
                commandMapper.selectOne(
                        Wrappers
                                .<DeviceCommandDO>lambdaQuery()
                                .eq(
                                        DeviceCommandDO::getRequestId,
                                        reply.requestId()
                                )
                                .last("LIMIT 1")
                );

        if (command == null) {
            throw new IllegalArgumentException(
                    "找不到设备命令 requestId="
                            + reply.requestId()
            );
        }

        command.setStatus(
                reply.success()
                        ? IotDeviceCommandStatus
                        .DEVICE_SUCCESS
                        .name()
                        : IotDeviceCommandStatus
                        .DEVICE_FAILURE
                        .name()
        );

        command.setReplyMessage(
                reply.message()
        );

        command.setReplyTime(
                LocalDateTime.now()
        );

        commandMapper.updateById(command);
    }

    public List<DeviceCommandVO> listByDevice(
            Long deviceId) {

        return commandMapper.selectList(
                        Wrappers
                                .<DeviceCommandDO>lambdaQuery()
                                .eq(
                                        DeviceCommandDO::getDeviceId,
                                        deviceId
                                )
                                .orderByDesc(
                                        DeviceCommandDO::getId
                                )
                )
                .stream()
                .map(this::toVO)
                .toList();
    }

    private DeviceCommandVO toVO(
            DeviceCommandDO command) {

        return new DeviceCommandVO(
                command.getId(),
                command.getDeviceId(),
                command.getRequestId(),
                command.getCommandType(),
                command.getCommandParams(),
                command.getStatus(),
                command.getReplyMessage(),
                command.getCreateTime(),
                command.getSentTime(),
                command.getReplyTime()
        );
    }
}