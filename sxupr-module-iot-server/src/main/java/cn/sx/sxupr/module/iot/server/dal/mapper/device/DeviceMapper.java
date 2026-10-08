package cn.sx.sxupr.module.iot.server.dal.mapper.device;

import cn.sx.sxupr.module.iot.server.dal.dataobject.device.DeviceDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceMapper
        extends BaseMapper<DeviceDO> {
}