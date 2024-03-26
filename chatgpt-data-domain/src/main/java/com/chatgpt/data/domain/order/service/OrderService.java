package com.chatgpt.data.domain.order.service;

import com.chatgpt.data.domain.order.model.aggregates.CreateOrderAggregate;
import com.chatgpt.data.domain.order.model.entity.OrderEntity;
import com.chatgpt.data.domain.order.model.entity.PayOrderEntity;
import com.chatgpt.data.domain.order.model.entity.ProductEntity;
import com.chatgpt.data.domain.order.model.valobj.OrderStatusVO;
import com.chatgpt.data.domain.order.model.valobj.PayStatusVO;
import com.chatgpt.data.domain.order.model.valobj.PayTypeVO;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 订单服务
 * @date 2024/3/25 23:05
 */
@Service
public class OrderService extends AbstractOrderService {

    @Value("${wxpay.config.appid}")
    private String appid;
    @Value("${wxpay.config.mchid}")
    private String mchid;
    @Value("${wxpay.config.notify-url}")
    private String notifyUrl;
    @Autowired(required = false)
    private NativePayService payService;

    @Override
    protected OrderEntity doSaveOrder(String openId, ProductEntity productEntity) {
        OrderEntity orderEntity = new OrderEntity();
        // 数据库有幂等拦截，如果有重复的订单ID会报错主键冲突。如果是公司里一般会有专门的雪花算法UUID服务
        orderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        orderEntity.setOrderTime(new Date());
        orderEntity.setOrderStatusVO(OrderStatusVO.CREATE);
        orderEntity.setTotalAmount(productEntity.getPrice());
        orderEntity.setPayTypeVO(PayTypeVO.WEIXIN_NATIVE);
        // 聚合信息
        CreateOrderAggregate aggregate = CreateOrderAggregate.builder()
                .openId(openId)
                .product(productEntity)
                .order(orderEntity)
                .build();
        // 保存订单；订单和支付，是2个操作。
        // 一个是数据库操作，一个是HTTP操作。所以不能一个事务处理，只能先保存订单再操作创建支付单，如果失败则需要任务补偿
        orderRepository.saveOrder(aggregate);
        return orderEntity;
    }

    @Override
    protected PayOrderEntity doPrepayOrder(String openId, String orderId, String productName, BigDecimal amountTotal) {
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(amountTotal.multiply(new BigDecimal(100)).intValue());
        request.setAmount(amount);
        request.setAppid(appid);
        request.setMchid(mchid);
        request.setDescription(productName);
        request.setNotifyUrl(notifyUrl);
        request.setOutTradeNo(orderId);

        // 创建微信支付单，如果你有多种支付方式，则可以根据支付类型的策略模式进行创建支付单
        String codeUrl = "";
        if (null != payService) {
            PrepayResponse prepay = payService.prepay(request);
            codeUrl = prepay.getCodeUrl();
        } else {
            codeUrl = "因你未配置支付渠道，所以暂时不能生成有效的支付URL。请配置支付渠道后，在application-dev.yml中配置支付渠道信息";
        }

        PayOrderEntity payOrderEntity = PayOrderEntity.builder()
                .openId(openId)
                .orderId(orderId)
                .payUrl(codeUrl)
                .payStatusVO(PayStatusVO.WAIT)
                .build();

        // 更新订单支付信息
        orderRepository.updateOrderPayInfo(payOrderEntity);
        return payOrderEntity;
    }
}
