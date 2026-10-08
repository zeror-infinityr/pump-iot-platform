CREATE TABLE iot_product
(
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '产品主键',
    product_key VARCHAR(64)   NOT NULL COMMENT '产品唯一标识',
    name        VARCHAR(100)  NOT NULL COMMENT '产品名称',
    description VARCHAR(500)           COMMENT '产品描述',
    status      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：1启用，0停用',
    create_time DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
                                      ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',

    PRIMARY KEY (id),
    UNIQUE KEY uk_product_key (product_key)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = 'IoT 产品表';