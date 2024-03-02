package com.chatgpt.data.domain.openai.service.rule.impl;

import cn.bugstack.chatglm.model.Model;
import com.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.chatgpt.data.domain.openai.model.annotation.LogicStrategy;
import com.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;
import com.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import com.chatgpt.data.domain.openai.service.rule.ILogicFilter;
import com.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 允许访问的模型过滤
 * @date 2024/3/2 13:25
 */

@Slf4j
@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModelEnum.MODEL_TYPE)
public class ModelTypeFilter implements ILogicFilter<UserAccountQuotaEntity> {
    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcessAggregate, UserAccountQuotaEntity data) {
        log.info("开始执行模型类型过滤；openId：{}，请求模型：{}，账号可用模型：{}", chatProcessAggregate.getOpenId(), chatProcessAggregate.getMessages(), data.getAllowModelTypeList());
        // 1.用户可用模型
        List<String> allowModelTypeLsit = data.getAllowModelTypeList();
        Model model = chatProcessAggregate.getModel();

        // 2.模型校验通过
        if (allowModelTypeLsit.contains(model.getCode())) {
            log.info("模型校验通过，放行！openId：{}", chatProcessAggregate.getOpenId());
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS)
                    .data(chatProcessAggregate)
                    .build();
        }

        // 3.模型校验拦截
        log.info("模型校验失败，拦截！openId：{}", chatProcessAggregate.getOpenId());
        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVO.REFUSE)
                .info("当前账户不支持使用 " + model + " 模型！可以联系客服升级账户。")
                .data(chatProcessAggregate)
                .build();
    }
}
