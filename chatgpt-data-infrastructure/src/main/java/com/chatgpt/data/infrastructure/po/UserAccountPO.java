package com.chatgpt.data.infrastructure.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description: 用户账户持久化对象
 * @date 2024/3/1 23:15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountPO {
    /**
     * 自增ID
     */
    private Long id;
    /**
     * 用户ID；这里用的是微信ID作为唯一ID，你也可以给用户创建唯一ID，之后绑定微信ID
     */
    private String openId;
    /**
     * 总量额度
     */
    private Integer totalQuota;
    /**
     * 剩余额度
     */
    private Integer surplusQuota;
    /**
     * 可用模型；CHATGLM_LITE,CHATGLM_LITE_32K,CHATGLM_STD,CHATGLM_PRO,CHATGLM_TURBO,GLM_3_5_TURB
     */
    private String modelTypes;
    /**
     * 账户状态；0-可用、1-冻结
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
