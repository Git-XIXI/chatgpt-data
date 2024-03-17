package com.chatgpt.data.domain.order.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 简单购物车实体对象
 * @date 2024/3/17 0:24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopCartEntity {
    /**
     * 用户微信唯一ID
     */
    private String openId;

    /**
     * 商品ID
     */
    private Integer productId;

    @Override
    public String toString() {
        return "ShopCartEntity{" +
                "openid='" + openId + '\'' +
                ", productId=" + productId +
                '}';
    }
}
