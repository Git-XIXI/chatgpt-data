DROP TABLE IF EXISTS `openai_product`;

CREATE TABLE `openai_product`
(
    `id`           bigint(20)     NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `product_id`   int(4)         NOT NULL COMMENT '商品ID',
    `product_name` varchar(32)    NOT NULL COMMENT '商品名称',
    `product_desc` varchar(128)   NOT NULL COMMENT '商品描述',
    `quota`        int(8)         NOT NULL COMMENT '额度次数',
    `price`        decimal(10, 2) NOT NULL COMMENT '商品价格',
    `sort`         int(4)         NOT NULL COMMENT '商品排序',
    `is_enabled`   tinyint(1)     NOT NULL DEFAULT '1' COMMENT '是否有效；0无效、1有效',
    `create_time`  datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
