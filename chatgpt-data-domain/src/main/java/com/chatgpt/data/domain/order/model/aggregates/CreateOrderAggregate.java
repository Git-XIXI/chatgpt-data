package com.chatgpt.data.domain.order.model.aggregates;

import com.chatgpt.data.domain.order.model.entity.OrderEntity;
import com.chatgpt.data.domain.order.model.entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 下单聚合对象
 * @date 2024/3/17 0:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderAggregate {
    /**
     * 用户ID
     */
    private String openId;
    /**
     * 商品
     */
    private ProductEntity product;
    /**
     * 订单
     */
    private OrderEntity order;
}
