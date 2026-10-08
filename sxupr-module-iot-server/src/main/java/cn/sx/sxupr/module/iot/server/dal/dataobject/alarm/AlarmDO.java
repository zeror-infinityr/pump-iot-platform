package cn.sx.sxupr.module.iot.server.dal.dataobject.alarm;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("iot_alarm")
public class AlarmDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long deviceId;

    private String alarmType;

    private String alarmLevel;

    private String status;

    private BigDecimal triggerValue;

    private BigDecimal thresholdValue;

    private LocalDateTime firstTriggerTime;

    private LocalDateTime lastTriggerTime;

    private LocalDateTime recoverTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTriggerValue() {
        return triggerValue;
    }

    public void setTriggerValue(BigDecimal triggerValue) {
        this.triggerValue = triggerValue;
    }

    public BigDecimal getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(BigDecimal thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public LocalDateTime getFirstTriggerTime() {
        return firstTriggerTime;
    }

    public void setFirstTriggerTime(LocalDateTime firstTriggerTime) {
        this.firstTriggerTime = firstTriggerTime;
    }

    public LocalDateTime getLastTriggerTime() {
        return lastTriggerTime;
    }

    public void setLastTriggerTime(LocalDateTime lastTriggerTime) {
        this.lastTriggerTime = lastTriggerTime;
    }

    public LocalDateTime getRecoverTime() {
        return recoverTime;
    }

    public void setRecoverTime(LocalDateTime recoverTime) {
        this.recoverTime = recoverTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}