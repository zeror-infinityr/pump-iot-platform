CREATE TABLE iot_device_command (
                                    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',

                                    device_id BIGINT NOT NULL COMMENT '设备ID',

                                    request_id VARCHAR(64) NOT NULL COMMENT '命令请求ID',

                                    command_type VARCHAR(64) NOT NULL COMMENT '命令类型',

                                    command_params VARCHAR(1000) NULL COMMENT '命令参数JSON',

                                    status VARCHAR(32) NOT NULL COMMENT '命令状态',

                                    reply_message VARCHAR(500) NULL COMMENT '设备回执',

                                    create_time DATETIME(3)
        NOT NULL
        DEFAULT CURRENT_TIMESTAMP(3),

                                    sent_time DATETIME(3) NULL,

                                    reply_time DATETIME(3) NULL,

                                    update_time DATETIME(3)
        NOT NULL
        DEFAULT CURRENT_TIMESTAMP(3)
        ON UPDATE CURRENT_TIMESTAMP(3),

                                    PRIMARY KEY (id),

                                    UNIQUE KEY uk_request_id (request_id),

                                    KEY idx_device_id (device_id),

                                    KEY idx_device_status (
        device_id,
        status
    )

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;