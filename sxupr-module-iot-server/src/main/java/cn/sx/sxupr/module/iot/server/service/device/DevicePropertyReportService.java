package cn.sx.sxupr.module.iot.server.service.device;

import cn.sx.sxupr.module.iot.core.enums.IotDeviceStatus;
import cn.sx.sxupr.module.iot.core.message.IotDevicePropertyMessage;
import cn.sx.sxupr.module.iot.server.controller.vo.device.DevicePropertyHistoryPointVO;
import cn.sx.sxupr.module.iot.server.controller.vo.device.DevicePropertyReportReqVO;
import cn.sx.sxupr.module.iot.server.controller.vo.device.DevicePropertyReportRespVO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.device.DeviceDO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.product.ProductDO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.thingmodel.ThingModelPropertyDO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.thingmodel.ThingModelVersionDO;
import cn.sx.sxupr.module.iot.server.dal.mapper.device.DeviceMapper;
import cn.sx.sxupr.module.iot.server.dal.mapper.product.ProductMapper;
import cn.sx.sxupr.module.iot.server.dal.mapper.thingmodel.ThingModelPropertyMapper;
import cn.sx.sxupr.module.iot.server.dal.mapper.thingmodel.ThingModelVersionMapper;
import cn.sx.sxupr.module.iot.server.infra.redis.DeviceLatestStateRedisStore;
import cn.sx.sxupr.module.iot.server.infra.tdengine.DevicePropertyHistoryStore;
import cn.sx.sxupr.module.iot.server.service.alarm.AlarmEvaluationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.sx.sxupr.module.iot.core.state.IotDeviceLatestState;
import cn.sx.sxupr.module.iot.server.infra.redis.DeviceLatestStateRedisStore;
import cn.sx.sxupr.module.iot.server.controller.vo.device.DeviceLatestStateVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class DevicePropertyReportService {

    private final ProductMapper productMapper;
    private final DeviceMapper deviceMapper;
    private final ThingModelVersionMapper versionMapper;
    private final ThingModelPropertyMapper propertyMapper;
    private final DeviceLatestStateRedisStore latestStateRedisStore;
    private final DevicePropertyHistoryStore historyStore;
    private final AlarmEvaluationService alarmEvaluationService;

    public DevicePropertyReportService(
            ProductMapper productMapper,
            DeviceMapper deviceMapper,
            ThingModelVersionMapper versionMapper,
            ThingModelPropertyMapper propertyMapper,
            DeviceLatestStateRedisStore latestStateRedisStore,
            DevicePropertyHistoryStore historyStore,
            AlarmEvaluationService alarmEvaluationService) {

        this.productMapper = productMapper;
        this.deviceMapper = deviceMapper;
        this.versionMapper = versionMapper;
        this.propertyMapper = propertyMapper;
        this.latestStateRedisStore = latestStateRedisStore;
        this.historyStore = historyStore;
        this.alarmEvaluationService = alarmEvaluationService;
    }

//    @Transactional(rollbackFor = Exception.class)
//    public DevicePropertyReportRespVO report(
//            DevicePropertyReportReqVO reqVO) {
//
//        IotDevicePropertyMessage message =
//                new IotDevicePropertyMessage(
//                        reqVO.requestId(),
//                        reqVO.productKey(),
//                        reqVO.deviceName(),
//                        reqVO.timestamp(),
//                        reqVO.properties()
//                );
//
//        ProductDO product = findProduct(message.productKey());
//
//        DeviceDO device =
//                findDevice(
//                        product.getId(),
//                        message.deviceName()
//                );
//
//        ThingModelVersionDO version =
//                findThingModelVersion(device);
//
//        List<ThingModelPropertyDO> definitions =
//                findPropertyDefinitions(version.getId());
//
//        validateProperties(
//                message.properties(),
//                definitions
//        );
//
//        Instant serverReceiveInstant =
//                Instant.now();
//
//        LocalDateTime serverReceiveTime =
//                LocalDateTime.ofInstant(
//                        serverReceiveInstant,
//                        ZoneId.systemDefault()
//                );
//
//        device.setOnlineStatus(
//                IotDeviceStatus.ONLINE.name()
//        );
//
//        device.setLastReportTime(serverReceiveTime);
//
//        deviceMapper.updateById(device);
//
//        IotDeviceLatestState latestState =
//                new IotDeviceLatestState(
//                        device.getId(),
//                        product.getProductKey(),
//                        device.getDeviceName(),
//                        version.getVersion(),
//                        message.requestId(),
//                        message.deviceTimestamp(),
//                        serverReceiveInstant.toEpochMilli(),
//                        message.properties()
//                );
//
//        latestStateRedisStore.save(latestState);
//
//        return new DevicePropertyReportRespVO(
//                message.requestId(),
//                device.getId(),
//                device.getDeviceName(),
//                message.deviceTimestamp(),
//                serverReceiveTime,
//                "ACCEPTED",
//                message.properties()
//        );
//    }

    public List<DevicePropertyHistoryPointVO> getHistory(
            Long deviceId,
            String identifier,
            int limit) {

        DeviceDO device =
                deviceMapper.selectById(
                        deviceId
                );

        if (device == null) {
            throw new IllegalArgumentException(
                    "设备不存在: " + deviceId
            );
        }

        return historyStore.query(
                deviceId,
                identifier,
                limit
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public DevicePropertyReportRespVO report(
            DevicePropertyReportReqVO reqVO) {

        IotDevicePropertyMessage message =
                new IotDevicePropertyMessage(
                        reqVO.requestId(),
                        reqVO.productKey(),
                        reqVO.deviceName(),
                        reqVO.timestamp(),
                        reqVO.properties()
                );

        return report(message);
    }

    @Transactional(rollbackFor = Exception.class)
    public DevicePropertyReportRespVO report(
            IotDevicePropertyMessage message) {

        ProductDO product =
                findProduct(message.productKey());

        DeviceDO device =
                findDevice(
                        product.getId(),
                        message.deviceName()
                );

        ThingModelVersionDO version =
                findThingModelVersion(device);

        List<ThingModelPropertyDO> definitions =
                findPropertyDefinitions(
                        version.getId()
                );

        validateProperties(
                message.properties(),
                definitions
        );

        Instant serverReceiveInstant =
                Instant.now();

        LocalDateTime serverReceiveTime =
                LocalDateTime.ofInstant(
                        serverReceiveInstant,
                        ZoneId.systemDefault()
                );

        device.setOnlineStatus(
                IotDeviceStatus.ONLINE.name()
        );

        device.setLastReportTime(
                serverReceiveTime
        );

//        deviceMapper.updateById(device);
//
//        IotDeviceLatestState latestState =
//                new IotDeviceLatestState(
//                        device.getId(),
//                        product.getProductKey(),
//                        device.getDeviceName(),
//                        version.getVersion(),
//                        message.requestId(),
//                        message.deviceTimestamp(),
//                        serverReceiveInstant.toEpochMilli(),
//                        message.properties()
//                );
//
//        latestStateRedisStore.save(
//                latestState
//        );
//
//        return new DevicePropertyReportRespVO(
//                message.requestId(),
//                device.getId(),
//                device.getDeviceName(),
//                message.deviceTimestamp(),
//                serverReceiveTime,
//                "ACCEPTED",
//                message.properties()
//        );
        deviceMapper.updateById(device);

        IotDeviceLatestState latestState =
                new IotDeviceLatestState(
                        device.getId(),
                        product.getProductKey(),
                        device.getDeviceName(),
                        version.getVersion(),
                        message.requestId(),
                        message.deviceTimestamp(),
                        serverReceiveInstant.toEpochMilli(),
                        message.properties()
                );

        latestStateRedisStore.save(
                latestState
        );

        historyStore.save(
                product,
                device,
                version,
                definitions,
                message,
                serverReceiveInstant
        );

        alarmEvaluationService.evaluate(
                device,
                message,
                serverReceiveTime
        );

        return new DevicePropertyReportRespVO(
                message.requestId(),
                device.getId(),
                device.getDeviceName(),
                message.deviceTimestamp(),
                serverReceiveTime,
                "ACCEPTED",
                message.properties()
        );
    }

    public DeviceLatestStateVO getLatestState(
            Long deviceId) {

        DeviceDO device =
                deviceMapper.selectById(deviceId);

        if (device == null) {
            throw new IllegalArgumentException(
                    "设备不存在: " + deviceId
            );
        }

        IotDeviceLatestState state =
                latestStateRedisStore.get(deviceId);

        if (state == null) {
            throw new IllegalStateException(
                    "设备尚无有效属性上报数据"
            );
        }

        return new DeviceLatestStateVO(
                state.deviceId(),
                state.productKey(),
                state.deviceName(),
                state.thingModelVersion(),
                state.requestId(),
                state.deviceTimestamp(),
                state.serverReceiveTimestamp(),
                state.properties()
        );
    }

    private ProductDO findProduct(String productKey) {

        ProductDO product =
                productMapper.selectOne(
                        new LambdaQueryWrapper<ProductDO>()
                                .eq(
                                        ProductDO::getProductKey,
                                        productKey
                                )
                );

        if (product == null) {
            throw new IllegalArgumentException(
                    "未知 productKey: " + productKey
            );
        }

        return product;
    }

    private DeviceDO findDevice(
            Long productId,
            String deviceName) {

        DeviceDO device =
                deviceMapper.selectOne(
                        new LambdaQueryWrapper<DeviceDO>()
                                .eq(
                                        DeviceDO::getProductId,
                                        productId
                                )
                                .eq(
                                        DeviceDO::getDeviceName,
                                        deviceName
                                )
                );

        if (device == null) {
            throw new IllegalArgumentException(
                    "设备不存在: " + deviceName
            );
        }

        return device;
    }

    private ThingModelVersionDO findThingModelVersion(
            DeviceDO device) {

        ThingModelVersionDO version =
                versionMapper.selectById(
                        device.getThingModelVersionId()
                );

        if (version == null) {
            throw new IllegalStateException(
                    "设备绑定的物模型版本不存在"
            );
        }

        return version;
    }

    private List<ThingModelPropertyDO> findPropertyDefinitions(
            Long thingModelVersionId) {

        return propertyMapper.selectList(
                new LambdaQueryWrapper<ThingModelPropertyDO>()
                        .eq(
                                ThingModelPropertyDO
                                        ::getThingModelVersionId,
                                thingModelVersionId
                        )
        );
    }

    private void validateProperties(
            Map<String, Object> reportedProperties,
            List<ThingModelPropertyDO> definitions) {

        Map<String, ThingModelPropertyDO> definitionMap =
                definitions.stream()
                        .collect(
                                Collectors.toMap(
                                        ThingModelPropertyDO::getIdentifier,
                                        Function.identity()
                                )
                        );

        // 1. 检查设备有没有上传物模型中不存在的属性
        for (Map.Entry<String, Object> entry
                : reportedProperties.entrySet()) {

            ThingModelPropertyDO definition =
                    definitionMap.get(entry.getKey());

            if (definition == null) {
                throw new IllegalArgumentException(
                        "物模型中不存在属性: "
                                + entry.getKey()
                );
            }

            validatePropertyValue(
                    definition,
                    entry.getValue()
            );
        }

        // 2. 检查所有必填属性有没有上报
        for (ThingModelPropertyDO definition
                : definitions) {

            if (definition.getRequiredFlag() == 1
                    && !reportedProperties.containsKey(
                    definition.getIdentifier())) {

                throw new IllegalArgumentException(
                        "缺少必填属性: "
                                + definition.getIdentifier()
                );
            }
        }
    }

    private void validatePropertyValue(
            ThingModelPropertyDO definition,
            Object value) {

        if (value == null) {
            throw new IllegalArgumentException(
                    "属性 "
                            + definition.getIdentifier()
                            + " 的值不能为 null"
            );
        }

        switch (definition.getDataType()) {

            case "DECIMAL" ->
                    validateDecimal(definition, value);

            case "INTEGER" ->
                    validateInteger(definition, value);

            case "BOOLEAN" -> {

                if (!(value instanceof Boolean)) {
                    throw new IllegalArgumentException(
                            "属性 "
                                    + definition.getIdentifier()
                                    + " 必须为 BOOLEAN"
                    );
                }
            }

            case "STRING" -> {

                if (!(value instanceof String)) {
                    throw new IllegalArgumentException(
                            "属性 "
                                    + definition.getIdentifier()
                                    + " 必须为 STRING"
                    );
                }
            }

            default ->
                    throw new IllegalStateException(
                            "不支持的数据类型: "
                                    + definition.getDataType()
                    );
        }
    }

    private void validateDecimal(
            ThingModelPropertyDO definition,
            Object value) {

        if (!(value instanceof Number)) {
            throw new IllegalArgumentException(
                    "属性 "
                            + definition.getIdentifier()
                            + " 必须为 DECIMAL"
            );
        }

        BigDecimal decimal =
                new BigDecimal(value.toString());

        validateRange(definition, decimal);
    }

    private void validateInteger(
            ThingModelPropertyDO definition,
            Object value) {

        if (!(value instanceof Number)) {
            throw new IllegalArgumentException(
                    "属性 "
                            + definition.getIdentifier()
                            + " 必须为 INTEGER"
            );
        }

        BigDecimal decimal =
                new BigDecimal(value.toString());

        if (decimal.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException(
                    "属性 "
                            + definition.getIdentifier()
                            + " 必须为整数"
            );
        }

        validateRange(definition, decimal);
    }

    private void validateRange(
            ThingModelPropertyDO definition,
            BigDecimal value) {

        if (definition.getMinValue() != null
                && value.compareTo(
                definition.getMinValue()) < 0) {

            throw new IllegalArgumentException(
                    "属性 "
                            + definition.getIdentifier()
                            + " 小于允许的最小值 "
                            + definition.getMinValue()
            );
        }

        if (definition.getMaxValue() != null
                && value.compareTo(
                definition.getMaxValue()) > 0) {

            throw new IllegalArgumentException(
                    "属性 "
                            + definition.getIdentifier()
                            + " 大于允许的最大值 "
                            + definition.getMaxValue()
            );
        }
    }
}