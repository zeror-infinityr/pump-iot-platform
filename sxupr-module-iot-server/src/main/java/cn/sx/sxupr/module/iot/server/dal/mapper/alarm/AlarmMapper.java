package cn.sx.sxupr.module.iot.server.dal.mapper.alarm;

import cn.sx.sxupr.module.iot.server.dal.dataobject.alarm.AlarmDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlarmMapper
        extends BaseMapper<AlarmDO> {
}