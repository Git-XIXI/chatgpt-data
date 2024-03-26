package com.chatgpt.data.domain.order.service;

import com.chatgpt.data.domain.order.model.aggregates.CreateOrderAggregate;
import com.chatgpt.data.domain.order.model.entity.PayOrderEntity;
import com.chatgpt.data.domain.order.model.entity.ProductEntity;
import com.chatgpt.data.domain.order.model.entity.ShopCartEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description: 订单服务 1.用户下单
 * @date 2024/3/17 01:01
 */
public interface IOrderService {

    /**
     * 用户下单，通过购物车信息，返回下单后的支付单
     * @param shopCartEntity 购物车实体
     * @return 支付单实体
     */
    PayOrderEntity createOrder(ShopCartEntity shopCartEntity);

    /**
     * 变更；订单支付成功
     * @param orderId 订单单号
     * @param transactionId 交易单号
     * @param totalAmount 订单金额
     * @param payTime   支付时间
     * @return true-更新成功，false-更新失败
     */
    boolean changeOrderPaySuccess(String orderId, String transactionId, BigDecimal totalAmount, Date payTime);

    /**
     * 查询订单信息
     * @param orderId 订单ID
     * @return 订单信息
     */
    CreateOrderAggregate queryOrder(String orderId);

    /**
     * 订单商品发货
     * @param orderId 订单ID
     */
    void deliverGoods(String orderId);

    /**
     * 查询待补货订单
     * @return 待补货订单列表
     */
    List<String> queryReplenishmentOrder();

    /**
     * 查询有效期内，未接收到支付回调的订单
     * @return 未接收到支付回调的订单列表
     */
    List<String> queryNoPayNotifyOrder();

    /**
     * 查询超时15分钟，未支付订单
     * @return 超时未支付订单列表
     */
    List<String> queryTimeoutCloseOrderList();

    /**
     * 变更；订单支付关闭
     * @param orderId 订单ID
     * @return true-更新成功，false-更新失败
     */
    boolean changeOrderClose(String orderId);

    /**
     * 查询商品列表
     * @return 商品列表
     */
    List<ProductEntity> queryProductList();

}
