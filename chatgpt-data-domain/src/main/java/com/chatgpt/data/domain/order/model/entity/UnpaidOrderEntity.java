package com.chatgpt.data.domain.order.model.entity;

import com.chatgpt.data.domain.order.model.valobj.PayStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description: 未支付的有效订单实体
 * @date 2024/3/17 0:41
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnpaidOrderEntity {
    /**
     * 用户ID
     */
    private String openId;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 订单金额
     */
    private BigDecimal totalAmount;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 支付地址；创建支付后，获得的url地址
     */
    private String payUrl;
    /**
     * 支付状态；0-等待支付、1-支付完成、2-支付失败、3-放弃支付
     */
    private PayStatusVO payStatus;
}
