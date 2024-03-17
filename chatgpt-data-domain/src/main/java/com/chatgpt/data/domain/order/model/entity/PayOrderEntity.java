package com.chatgpt.data.domain.order.model.entity;

import com.chatgpt.data.domain.order.model.valobj.PayStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 支付单实体
 * @date 2024/3/16 23:52
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderEntity {
    /**
     * 用户ID
     */
    private String openId;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 支付地址
     */
    private String payUrl;
    /**
     * 支付状态；0-等待支付、1-支付完成、2-支付失败、3-放弃支付
     */
    private PayStatusVO payStatusVO;

    @Override
    public String toString() {
        return "PayOrderEntity{" +
                "openid='" + openId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", payUrl='" + payUrl + '\'' +
                ", payStatus=" + payStatusVO.getCode() + ": " + payStatusVO.getDesc() +
                '}';
    }

}
