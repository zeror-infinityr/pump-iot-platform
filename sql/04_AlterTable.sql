CREATE TABLE iot_alarm (
                           id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',

                           device_id BIGINT NOT NULL COMMENT '设备ID',

                           alarm_type VARCHAR(64) NOT NULL COMMENT '告警类型',

                           alarm_level VARCHAR(32) NOT NULL COMMENT '告警级别',

                           status VARCHAR(32) NOT NULL COMMENT 'ACTIVE/RECOVERED',

                           trigger_value DECIMAL(20,6) NULL COMMENT '最近触发值',

                           threshold_value DECIMAL(20,6) NULL COMMENT '触发阈值',

                           first_trigger_time DATETIME(3) NOT NULL COMMENT '首次触发时间',

                           last_trigger_time DATETIME(3) NOT NULL COMMENT '最近触发时间',

                           recover_time DATETIME(3) NULL COMMENT '恢复时间',

                           create_time DATETIME(3)
        NOT NULL
        DEFAULT CURRENT_TIMESTAMP(3),

                           update_time DATETIME(3)
        NOT NULL
        DEFAULT CURRENT_TIMESTAMP(3)
        ON UPDATE CURRENT_TIMESTAMP(3),

                           PRIMARY KEY (id),

                           KEY idx_device_status (
        device_id,
        status
    ),

                           KEY idx_device_type (
        device_id,
        alarm_type
    )

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='IoT设备告警表';