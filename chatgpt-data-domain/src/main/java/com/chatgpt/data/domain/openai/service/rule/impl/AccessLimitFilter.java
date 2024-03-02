package com.chatgpt.data.domain.openai.service.rule.impl;

import com.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.chatgpt.data.domain.openai.model.annotation.LogicStrategy;
import com.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;
import com.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import com.chatgpt.data.domain.openai.service.rule.ILogicFilter;
import com.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @description: 访问次数限制
 * @date 2024/3/1 0:35
 */
@Slf4j
@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModelEnum.ACCESS_LIMIT)
public class AccessLimitFilter implements ILogicFilter<UserAccountQuotaEntity> {

    @Value("${app.config.white-list}")
    private String whiteListStr;
    @Value("${app.config.limit-count:10}")
    private Integer limitCount;
    @Resource
    private Cache<String, Integer> visitCache;

    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcessAggregate, UserAccountQuotaEntity data) throws ExecutionException {
        log.info("开始执行访问次数限制；openId：{}", chatProcessAggregate.getOpenId());
        // 1.白名单用户直接放行
        if (chatProcessAggregate.isWhiteList(whiteListStr)) {
            log.info("白名单用户放行；openId：{}", chatProcessAggregate.getOpenId());
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS)
                    .data(chatProcessAggregate)
                    .build();
        }
        String openId = chatProcessAggregate.getOpenId();

        // 2. 个人账户不为空，不做系统访问次数拦截
        if (null != data) {
            log.info("个人账户不为空；不做系统访问次数拦截，openId：{}", chatProcessAggregate.getOpenId());
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS).data(chatProcessAggregate).build();
        }

        // 3.访问次数判断
        int visitCount = visitCache.get(openId, () -> 0);
        if (visitCount < limitCount) {
            log.info("访问次数剩余{}次；openId：{}", visitCount, chatProcessAggregate.getOpenId());
            visitCache.put(openId, visitCount + 1);
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS)
                    .data(chatProcessAggregate)
                    .build();
        }
        log.info("访问次数超过限制；openId：{}", chatProcessAggregate.getOpenId());
        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .info("您今日的免费次数已耗尽！")
                .type(LogicCheckTypeVO.REFUSE)
                .data(chatProcessAggregate)
                .build();
    }


}
