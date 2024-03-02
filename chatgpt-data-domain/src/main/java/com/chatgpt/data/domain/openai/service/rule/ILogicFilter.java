package com.chatgpt.data.domain.openai.service.rule;

import com.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;

import java.util.concurrent.ExecutionException;

/**
 * @description: 规则过滤接口
 * @date 2024/3/1 0:35
 */
public interface ILogicFilter<T> {
    RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcessAggregate, T data) throws ExecutionException;
}
