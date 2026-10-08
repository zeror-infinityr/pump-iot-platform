CREATE TABLE iot_device (
                            id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',

                            product_id BIGINT NOT NULL COMMENT '所属产品ID',

                            thing_model_version_id BIGINT NOT NULL COMMENT '绑定的物模型版本ID',

                            device_key VARCHAR(64) NOT NULL COMMENT '设备唯一标识',

                            device_name VARCHAR(64) NOT NULL COMMENT '设备名称',

                            device_secret VARCHAR(128) NOT NULL COMMENT '设备认证密钥',

                            online_status VARCHAR(16) NOT NULL DEFAULT 'OFFLINE'
                                COMMENT '在线状态：ONLINE/OFFLINE',

                            last_report_time DATETIME(3) NULL COMMENT '最近一次有效上报时间',

                            create_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

                            update_time DATETIME(3) NOT NULL
        DEFAULT CURRENT_TIMESTAMP(3)
        ON UPDATE CURRENT_TIMESTAMP(3),

                            PRIMARY KEY (id),

                            UNIQUE KEY uk_device_key (device_key),

                            UNIQUE KEY uk_product_device_name (
                                product_id,
                                device_name
                                ),

                            KEY idx_product_id (product_id),

                            KEY idx_thing_model_version_id (
        thing_model_version_id
    ),

                            KEY idx_online_status (online_status)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='IoT设备表';