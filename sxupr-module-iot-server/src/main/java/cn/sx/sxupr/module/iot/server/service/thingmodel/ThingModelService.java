package cn.sx.sxupr.module.iot.server.service.thingmodel;

import cn.sx.sxupr.module.iot.server.controller.vo.thingmodel.ThingModelPropertyCreateReqVO;
import cn.sx.sxupr.module.iot.server.controller.vo.thingmodel.ThingModelPropertyVO;
import cn.sx.sxupr.module.iot.server.controller.vo.thingmodel.ThingModelVersionCreateReqVO;
import cn.sx.sxupr.module.iot.server.controller.vo.thingmodel.ThingModelVersionVO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.thingmodel.ThingModelPropertyDO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.thingmodel.ThingModelVersionDO;
import cn.sx.sxupr.module.iot.server.dal.mapper.thingmodel.ThingModelPropertyMapper;
import cn.sx.sxupr.module.iot.server.dal.mapper.thingmodel.ThingModelVersionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ThingModelService {

    private final ThingModelVersionMapper versionMapper;
    private final ThingModelPropertyMapper propertyMapper;

    public ThingModelService(
            ThingModelVersionMapper versionMapper,
            ThingModelPropertyMapper propertyMapper) {

        this.versionMapper = versionMapper;
        this.propertyMapper = propertyMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public ThingModelVersionVO createVersion(
            Long productId,
            ThingModelVersionCreateReqVO reqVO) {

        validateProperties(reqVO.properties());

        ThingModelVersionDO latest = versionMapper.selectOne(
                new LambdaQueryWrapper<ThingModelVersionDO>()
                        .eq(ThingModelVersionDO::getProductId, productId)
                        .orderByDesc(ThingModelVersionDO::getVersion)
                        .last("LIMIT 1")
        );

        int nextVersion =
                latest == null ? 1 : latest.getVersion() + 1;

        ThingModelVersionDO versionDO = new ThingModelVersionDO();

        versionDO.setProductId(productId);
        versionDO.setVersion(nextVersion);
        versionDO.setDescription(reqVO.description());

        versionMapper.insert(versionDO);

        for (ThingModelPropertyCreateReqVO propertyReq
                : reqVO.properties()) {

            ThingModelPropertyDO propertyDO =
                    new ThingModelPropertyDO();

            propertyDO.setThingModelVersionId(versionDO.getId());
            propertyDO.setIdentifier(propertyReq.identifier());
            propertyDO.setName(propertyReq.name());
            propertyDO.setDataType(propertyReq.dataType().name());
            propertyDO.setUnit(propertyReq.unit());
            propertyDO.setMinValue(propertyReq.minValue());
            propertyDO.setMaxValue(propertyReq.maxValue());

            propertyDO.setRequiredFlag(
                    Boolean.FALSE.equals(propertyReq.required())
                            ? 0
                            : 1
            );

            propertyMapper.insert(propertyDO);
        }

        return getLatest(productId);
    }

    public ThingModelVersionVO getLatest(Long productId) {

        ThingModelVersionDO versionDO =
                versionMapper.selectOne(
                        new LambdaQueryWrapper<ThingModelVersionDO>()
                                .eq(
                                        ThingModelVersionDO::getProductId,
                                        productId
                                )
                                .orderByDesc(
                                        ThingModelVersionDO::getVersion
                                )
                                .last("LIMIT 1")
                );

        if (versionDO == null) {
            return null;
        }

        List<ThingModelPropertyDO> propertyDOList =
                propertyMapper.selectList(
                        new LambdaQueryWrapper<ThingModelPropertyDO>()
                                .eq(
                                        ThingModelPropertyDO
                                                ::getThingModelVersionId,
                                        versionDO.getId()
                                )
                                .orderByAsc(
                                        ThingModelPropertyDO::getId
                                )
                );

        List<ThingModelPropertyVO> properties =
                propertyDOList.stream()
                        .map(propertyDO ->
                                new ThingModelPropertyVO(
                                        propertyDO.getId(),
                                        propertyDO.getIdentifier(),
                                        propertyDO.getName(),
                                        propertyDO.getDataType(),
                                        propertyDO.getUnit(),
                                        propertyDO.getMinValue(),
                                        propertyDO.getMaxValue(),
                                        propertyDO.getRequiredFlag() == 1
                                )
                        )
                        .toList();

        return new ThingModelVersionVO(
                versionDO.getId(),
                versionDO.getProductId(),
                versionDO.getVersion(),
                versionDO.getDescription(),
                versionDO.getCreateTime(),
                versionDO.getUpdateTime(),
                properties
        );
    }

    private void validateProperties(
            List<ThingModelPropertyCreateReqVO> properties) {

        Set<String> identifiers = new HashSet<>();

        for (ThingModelPropertyCreateReqVO property : properties) {

            if (!identifiers.add(property.identifier())) {
                throw new IllegalArgumentException(
                        "属性标识符重复: "
                                + property.identifier()
                );
            }

            if (property.minValue() != null
                    && property.maxValue() != null
                    && property.minValue()
                    .compareTo(property.maxValue()) > 0) {

                throw new IllegalArgumentException(
                        "属性 "
                                + property.identifier()
                                + " 的 minValue 不能大于 maxValue"
                );
            }
        }
    }
}