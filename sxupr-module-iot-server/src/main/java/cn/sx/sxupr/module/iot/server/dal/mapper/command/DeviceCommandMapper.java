package cn.sx.sxupr.module.iot.server.dal.mapper.command;

import cn.sx.sxupr.module.iot.server.dal.dataobject.command.DeviceCommandDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceCommandMapper
        extends BaseMapper<DeviceCommandDO> {
}