package com.chatgpt.data.infrastructure.repository;

import com.chatgpt.data.domain.order.model.aggregates.CreateOrderAggregate;
import com.chatgpt.data.domain.order.model.entity.*;
import com.chatgpt.data.domain.order.model.valobj.OrderStatusVO;
import com.chatgpt.data.domain.order.model.valobj.PayStatusVO;
import com.chatgpt.data.domain.order.repository.IOrderRepository;
import com.chatgpt.data.infrastructure.dao.IOpenAIOrderDao;
import com.chatgpt.data.infrastructure.dao.IOpenAIProductDao;
import com.chatgpt.data.infrastructure.dao.IUserAccountDao;
import com.chatgpt.data.infrastructure.po.OpenAIOrderPO;
import com.chatgpt.data.infrastructure.po.OpenAIProductPO;
import com.chatgpt.data.infrastructure.po.UserAccountPO;
import com.chatgpt.data.types.enums.OpenAIProductEnableModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description: 订单仓储服务
 * @date 2024/3/24 17:07
 */

@Repository
public class OrderRepository implements IOrderRepository {
    @Resource
    private IOpenAIOrderDao openAIOrderDao;
    @Resource
    private IOpenAIProductDao openAIProductDao;
    @Resource
    private IUserAccountDao userAccountDao;

    @Override
    public UnpaidOrderEntity queryUnpaidOrder(ShopCartEntity shopCartEntity) {
        OpenAIOrderPO openAIOrderPO = new OpenAIOrderPO();
        openAIOrderPO.setOpenId(shopCartEntity.getOpenId());
        openAIOrderPO.setProductId(shopCartEntity.getProductId());
        OpenAIOrderPO openAIOrderPORes = openAIOrderDao.queryUnpaidOrder(openAIOrderPO);
        if (null == openAIOrderPORes) {
            return null;
        }
        return UnpaidOrderEntity.builder()
                .openId(shopCartEntity.getOpenId())
                .orderId(openAIOrderPORes.getOrderId())
                .productName(openAIOrderPORes.getProductName())
                .totalAmount(openAIOrderPORes.getTotalAmount())
                .payUrl(openAIOrderPORes.getPayUrl())
                .payStatus(PayStatusVO.get(openAIOrderPORes.getPayStatus()))
                .build();
    }

    @Override
    public ProductEntity queryProduct(Integer productId) {
        OpenAIProductPO openAIProductPO = openAIProductDao.queryProductByProductId(productId);
        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(openAIProductPO.getProductId());
        productEntity.setProductName(openAIProductPO.getProductName());
        productEntity.setProductDesc(openAIProductPO.getProductDesc());
        productEntity.setQuota(openAIProductPO.getQuota());
        productEntity.setPrice(openAIProductPO.getPrice());
        productEntity.setEnable(OpenAIProductEnableModel.get(openAIProductPO.getIsEnabled()));
        return productEntity;
    }

    @Override
    public void saveOrder(CreateOrderAggregate aggregate) {
        String openid = aggregate.getOpenId();
        ProductEntity product = aggregate.getProduct();
        OrderEntity order = aggregate.getOrder();
        OpenAIOrderPO openAIOrderPO = new OpenAIOrderPO();
        openAIOrderPO.setOpenId(openid);
        openAIOrderPO.setProductId(product.getProductId());
        openAIOrderPO.setProductName(product.getProductName());
        openAIOrderPO.setProductQuota(product.getQuota());
        openAIOrderPO.setOrderId(order.getOrderId());
        openAIOrderPO.setOrderTime(order.getOrderTime());
        openAIOrderPO.setOrderStatus(order.getOrderStatusVO().getCode());
        openAIOrderPO.setTotalAmount(order.getTotalAmount());
        openAIOrderPO.setPayType(order.getPayTypeVO().getCode());
        openAIOrderPO.setPayStatus(PayStatusVO.WAIT.getCode());
        openAIOrderDao.insert(openAIOrderPO);
    }

    @Override
    public void updateOrderPayInfo(PayOrderEntity payOrderEntity) {
        OpenAIOrderPO openAIOrderPO = new OpenAIOrderPO();
        openAIOrderPO.setOpenId(payOrderEntity.getOpenId());
        openAIOrderPO.setOrderId(payOrderEntity.getOrderId());
        openAIOrderPO.setPayUrl(payOrderEntity.getPayUrl());
        openAIOrderPO.setPayStatus(payOrderEntity.getPayStatusVO().getCode());
        openAIOrderDao.updateOrderPayInfo(openAIOrderPO);
    }

    @Override
    public boolean changeOrderPaySuccess(String orderId, String transactionId, BigDecimal totalAmount, Date payTime) {
        OpenAIOrderPO openAIOrderPO = new OpenAIOrderPO();
        openAIOrderPO.setOrderId(orderId);
        openAIOrderPO.setPayAmount(totalAmount);
        openAIOrderPO.setPayTime(payTime);
        openAIOrderPO.setTransactionId(transactionId);
        int count = openAIOrderDao.changeOrderPaySuccess(openAIOrderPO);
        return count == 1;
    }

    @Override
    public CreateOrderAggregate queryOrder(String orderId) {
        OpenAIOrderPO openAIOrderPO = openAIOrderDao.queryOrder(orderId);

        ProductEntity product = new ProductEntity();
        product.setProductId(openAIOrderPO.getProductId());
        product.setProductName(openAIOrderPO.getProductName());

        OrderEntity order = new OrderEntity();
        order.setOrderId(openAIOrderPO.getOrderId());
        order.setOrderTime(openAIOrderPO.getOrderTime());
        order.setOrderStatusVO(OrderStatusVO.get(openAIOrderPO.getOrderStatus()));
        order.setTotalAmount(openAIOrderPO.getTotalAmount());

        CreateOrderAggregate createOrderAggregate = new CreateOrderAggregate();
        createOrderAggregate.setOpenId(openAIOrderPO.getOpenId());
        createOrderAggregate.setOrder(order);
        createOrderAggregate.setProduct(product);

        return createOrderAggregate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 350, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public void deliverGoods(String orderId) {
        OpenAIOrderPO openAIOrderPO = openAIOrderDao.queryOrder(orderId);

        // 1.变更发货状态
        int updateOrderStatusDeliverGoodsBount = openAIOrderDao.updateOrderStatusDeliverGoods(orderId);
        if (1 != updateOrderStatusDeliverGoodsBount) {
            throw new RuntimeException("updateOrderStatusDeliverGoodsCount update count is not equal 1");
        }

        // 2.账户额度变更
        UserAccountPO userAccountPO = userAccountDao.queryUserAccount(openAIOrderPO.getOpenId());
        UserAccountPO userAccountPOReq = new UserAccountPO();
        userAccountPOReq.setOpenId(openAIOrderPO.getOpenId());
        userAccountPOReq.setTotalQuota(openAIOrderPO.getProductQuota());
        userAccountPOReq.setSurplusQuota(openAIOrderPO.getProductQuota());
        if (null != userAccountPO) {
            int addAccountQuotaCount = userAccountDao.addAccountQuota(userAccountPOReq);
            if (1 != addAccountQuotaCount) {
                throw new RuntimeException("addAccountQuotaCount update count is not equal 1");
            }
        } else {
            userAccountDao.insert(userAccountPOReq);
        }
    }

    @Override
    public List<String> queryReplenishmentOrder() {
        return openAIOrderDao.queryReplenishmentOrder();
    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return openAIOrderDao.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return openAIOrderDao.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return openAIOrderDao.changeOrderClose(orderId);
    }

    @Override
    public List<ProductEntity> queryProductList() {
        List<OpenAIProductPO> openAIProductPOList = openAIProductDao.queryProductList();
        List<ProductEntity> productEntityList = new ArrayList<>(openAIProductPOList.size());
        for (OpenAIProductPO openAIProductPO : openAIProductPOList) {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setProductId(openAIProductPO.getProductId());
            productEntity.setProductName(openAIProductPO.getProductName());
            productEntity.setProductDesc(openAIProductPO.getProductDesc());
            productEntity.setQuota(openAIProductPO.getQuota());
            productEntity.setPrice(openAIProductPO.getPrice());
            productEntityList.add(productEntity);
        }
        return productEntityList;
    }
}
