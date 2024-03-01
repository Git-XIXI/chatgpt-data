package com.chatgpt.data.domain.openai.service.rule.impl;

import com.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.chatgpt.data.domain.openai.model.annotation.LogicStrategy;
import com.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVo;
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
public class AccessLimitFilter implements ILogicFilter {

    @Value("${app.config.white-list}")
    private String whiteListStr;
    @Value("${app.config.limit-count:10}")
    private Integer limitCount;
    @Resource
    private Cache<String, Integer> visitCache;

    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcessAggregate) throws ExecutionException {
        // 1.白名单用户直接放行
        if (chatProcessAggregate.isWhiteList(whiteListStr)) {
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVo.SUCCESS)
                    .data(chatProcessAggregate)
                    .build();
        }
        String openId = chatProcessAggregate.getOpenId();

        // 2.访问次数判断
        int visitCount = visitCache.get(openId, () -> 0);
        if (visitCount < limitCount) {
            visitCache.put(openId, visitCount + 1);
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVo.SUCCESS)
                    .data(chatProcessAggregate)
                    .build();
        }

        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .info("您今日的免费次数已耗尽！")
                .type(LogicCheckTypeVo.REFUSE)
                .data(chatProcessAggregate)
                .build();
    }


}
