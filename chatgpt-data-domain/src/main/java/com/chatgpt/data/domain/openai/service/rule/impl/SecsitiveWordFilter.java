package com.chatgpt.data.domain.openai.service.rule.impl;

import com.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.chatgpt.data.domain.openai.model.annotation.LogicStrategy;
import com.chatgpt.data.domain.openai.model.entity.MessageEntity;
import com.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVo;
import com.chatgpt.data.domain.openai.service.rule.ILogicFilter;
import com.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 敏感词过滤
 * @date 2024/3/1 15:56
 */
@Slf4j
@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModelEnum.SENSITIVE_WORD)
public class SecsitiveWordFilter implements ILogicFilter {
    @Resource
    private SensitiveWordBs words;

    @Value("${app.config.white-list}")
    private String whiteListStr;

    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcessAggregate) {
        // 白名单用户不做敏感词处理
        if (chatProcessAggregate.isWhiteList(whiteListStr)) {
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVo.SUCCESS).data(chatProcessAggregate).build();
        }

        ChatProcessAggregate newChatProcessAggregate = new ChatProcessAggregate();
        newChatProcessAggregate.setOpenId(chatProcessAggregate.getOpenId());
        newChatProcessAggregate.setModel(chatProcessAggregate.getModel());

        List<MessageEntity> newMessages = chatProcessAggregate.getMessages().stream()
                .map(message -> {
                    String content = message.getContent();
                    log.info("原始内容：{}", content);
                    String replace = words.replace(content);
                    log.info("替换后内容：{}", replace);
                    return MessageEntity.builder()
                            .role(message.getRole())
                            .content(replace)
                            .build();
                }).collect(Collectors.toList());
        newChatProcessAggregate.setMessages(newMessages);
        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVo.SUCCESS)
                .data(newChatProcessAggregate)
                .build();
    }
}
