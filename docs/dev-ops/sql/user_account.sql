CREATE database if NOT EXISTS chatgpt_database default character set utf8mb4 collate utf8mb4_0900_ai_ci;

use chatgpt_database;

CREATE TABLE `user_account`
(
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `openid`        varchar(64)  NOT NULL COMMENT '用户ID；用微信ID作为唯一ID',
    `total_quota`   int(11)      NOT NULL DEFAULT '0' COMMENT '总量额度；分配的总使用次数',
    `surplus_quota` int(11)      NOT NULL DEFAULT '0' COMMENT '剩余额度；剩余的可使用次数',
    `model_types`   varchar(128) NOT NULL COMMENT '可用模型；CHATGLM_LITE,CHATGLM_LITE_32K,CHATGLM_STD,CHATGLM_PRO,CHATGLM_TURBO,GLM_3_5_TURB',
    `status`        tinyint(1)   NOT NULL DEFAULT '0' COMMENT '账户状态；0-可用、1-冻结',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_openid` (`openid`),
    KEY `idx_surplus_quota_status` (`surplus_quota`, `status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;