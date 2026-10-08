package cn.sx.sxupr.module.iot.server.service.device;

import cn.sx.sxupr.module.iot.core.enums.IotDeviceStatus;
import cn.sx.sxupr.module.iot.core.util.IotDeviceCredentialUtils;
import cn.sx.sxupr.module.iot.server.controller.vo.device.DeviceCreateReqVO;
import cn.sx.sxupr.module.iot.server.controller.vo.device.DeviceCreateRespVO;
import cn.sx.sxupr.module.iot.server.controller.vo.device.DeviceSecretResetRespVO;
import cn.sx.sxupr.module.iot.server.controller.vo.device.DeviceVO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.device.DeviceDO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.product.ProductDO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.thingmodel.ThingModelVersionDO;
import cn.sx.sxupr.module.iot.server.dal.mapper.device.DeviceMapper;
import cn.sx.sxupr.module.iot.server.dal.mapper.product.ProductMapper;
import cn.sx.sxupr.module.iot.server.dal.mapper.thingmodel.ThingModelVersionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeviceService {

    private final DeviceMapper deviceMapper;
    private final ProductMapper productMapper;
    private final ThingModelVersionMapper versionMapper;

    public DeviceService(
            DeviceMapper deviceMapper,
            ProductMapper productMapper,
            ThingModelVersionMapper versionMapper) {

        this.deviceMapper = deviceMapper;
        this.productMapper = productMapper;
        this.versionMapper = versionMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public DeviceCreateRespVO createDevice(
            DeviceCreateReqVO reqVO) {

        ProductDO product =
                productMapper.selectById(reqVO.productId());

        if (product == null) {
            throw new IllegalArgumentException(
                    "产品不存在: " + reqVO.productId()
            );
        }

        Long count = deviceMapper.selectCount(
                new LambdaQueryWrapper<DeviceDO>()
                        .eq(
                                DeviceDO::getProductId,
                                reqVO.productId()
                        )
                        .eq(
                                DeviceDO::getDeviceName,
                                reqVO.deviceName()
                        )
        );

        if (count > 0) {
            throw new IllegalArgumentException(
                    "该产品下设备名称已存在: "
                            + reqVO.deviceName()
            );
        }

        ThingModelVersionDO latestVersion =
                versionMapper.selectOne(
                        new LambdaQueryWrapper<ThingModelVersionDO>()
                                .eq(
                                        ThingModelVersionDO::getProductId,
                                        reqVO.productId()
                                )
                                .orderByDesc(
                                        ThingModelVersionDO::getVersion
                                )
                                .last("LIMIT 1")
                );

        if (latestVersion == null) {
            throw new IllegalStateException(
                    "产品尚未定义物模型，不能创建设备"
            );
        }

        String deviceKey =
                IotDeviceCredentialUtils.generateDeviceKey();

        String deviceSecret =
                IotDeviceCredentialUtils.generateDeviceSecret();

        DeviceDO deviceDO = new DeviceDO();

        deviceDO.setProductId(reqVO.productId());
        deviceDO.setThingModelVersionId(latestVersion.getId());
        deviceDO.setDeviceKey(deviceKey);
        deviceDO.setDeviceName(reqVO.deviceName());
        deviceDO.setDeviceSecret(deviceSecret);
        deviceDO.setOnlineStatus(
                IotDeviceStatus.OFFLINE.name()
        );

        deviceMapper.insert(deviceDO);

        DeviceVO device =
                buildDeviceVO(
                        deviceDO.getId(),
                        product,
                        latestVersion
                );

        return new DeviceCreateRespVO(
                device,
                deviceSecret
        );
    }

    public List<DeviceVO> listDevices() {

        return deviceMapper.selectList(
                        new LambdaQueryWrapper<DeviceDO>()
                                .orderByDesc(DeviceDO::getId)
                )
                .stream()
                .map(deviceDO ->
                        buildDeviceVO(deviceDO.getId())
                )
                .toList();
    }

    public DeviceVO getDevice(Long id) {

        return buildDeviceVO(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public DeviceSecretResetRespVO resetSecret(
            Long deviceId) {

        DeviceDO deviceDO =
                deviceMapper.selectById(deviceId);

        if (deviceDO == null) {
            throw new IllegalArgumentException(
                    "设备不存在: " + deviceId
            );
        }

        String newSecret =
                IotDeviceCredentialUtils.generateDeviceSecret();

        deviceDO.setDeviceSecret(newSecret);

        deviceMapper.updateById(deviceDO);

        return new DeviceSecretResetRespVO(
                deviceDO.getId(),
                deviceDO.getDeviceKey(),
                newSecret
        );
    }

    private DeviceVO buildDeviceVO(Long deviceId) {

        DeviceDO deviceDO =
                deviceMapper.selectById(deviceId);

        if (deviceDO == null) {
            throw new IllegalArgumentException(
                    "设备不存在: " + deviceId
            );
        }

        ProductDO product =
                productMapper.selectById(
                        deviceDO.getProductId()
                );

        ThingModelVersionDO version =
                versionMapper.selectById(
                        deviceDO.getThingModelVersionId()
                );

        return toVO(deviceDO, product, version);
    }

    private DeviceVO buildDeviceVO(
            Long deviceId,
            ProductDO product,
            ThingModelVersionDO version) {

        DeviceDO deviceDO =
                deviceMapper.selectById(deviceId);

        return toVO(deviceDO, product, version);
    }

    private DeviceVO toVO(
            DeviceDO deviceDO,
            ProductDO product,
            ThingModelVersionDO version) {

        return new DeviceVO(
                deviceDO.getId(),
                deviceDO.getProductId(),
                product.getProductKey(),
                deviceDO.getDeviceKey(),
                deviceDO.getDeviceName(),
                version.getVersion(),
                deviceDO.getOnlineStatus(),
                deviceDO.getLastReportTime(),
                deviceDO.getCreateTime(),
                deviceDO.getUpdateTime()
        );
    }
}