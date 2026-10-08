package cn.sx.sxupr.module.iot.server.dal.mapper.thingmodel;

import cn.sx.sxupr.module.iot.server.dal.dataobject.thingmodel.ThingModelVersionDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ThingModelVersionMapper
        extends BaseMapper<ThingModelVersionDO> {
}