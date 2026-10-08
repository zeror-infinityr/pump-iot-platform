package cn.sx.sxupr.module.iot.server.dal.dataobject.command;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("iot_device_command")
public class DeviceCommandDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long deviceId;

    private String requestId;

    private String commandType;

    private String commandParams;

    private String status;

    private String replyMessage;

    private LocalDateTime createTime;

    private LocalDateTime sentTime;

    private LocalDateTime replyTime;

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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public String getCommandParams() {
        return commandParams;
    }

    public void setCommandParams(String commandParams) {
        this.commandParams = commandParams;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    public LocalDateTime getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(LocalDateTime replyTime) {
        this.replyTime = replyTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
}