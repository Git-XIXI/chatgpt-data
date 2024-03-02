package com.chatgpt.data.domain.openai.service.rule.impl;

import com.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.chatgpt.data.domain.openai.model.annotation.LogicStrategy;
import com.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;
import com.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import com.chatgpt.data.domain.openai.repository.IOpenAiRepository;
import com.chatgpt.data.domain.openai.service.rule.ILogicFilter;
import com.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description: 允许访问的模型过滤
 * @date 2024/3/2 13:25
 */

@Slf4j
@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModelEnum.USER_QUOTA)
public class UserQuotaFilter implements ILogicFilter<UserAccountQuotaEntity> {
    @Resource
    private IOpenAiRepository openAiRepository;

    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcessAggregate, UserAccountQuotaEntity data) {
        log.info("开始执行账户额度校验；openId：{}，剩余额度：{}", chatProcessAggregate.getOpenId(), data.getSurplusQuota());
        if (data.getSurplusQuota() > 0) {
            // 扣减账户额度；因为是个人账户数据，无资源竞争，所以直接使用数据库也可以。但为了效率，也可以优化为 Redis 扣减。
            int updateCount = openAiRepository.subAccountQuota(data.getOpenId());
            if (0 != updateCount) {
                return RuleLogicEntity.<ChatProcessAggregate>builder()
                        .type(LogicCheckTypeVO.SUCCESS)
                        .data(chatProcessAggregate)
                        .build();
            }
            log.info("账户额度校验失败，更新数据库失败！");
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .info("个人账户，总额度【" + data.getTotalQuota() + "】次，已耗尽！")
                    .type(LogicCheckTypeVO.REFUSE).data(chatProcessAggregate).build();
        }
        log.info("账户额度校验失败，剩余额度为 0！");
        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .info("个人账户，总额度【" + data.getTotalQuota() + "】次，已耗尽！")
                .type(LogicCheckTypeVO.REFUSE).data(chatProcessAggregate).build();
    }

}
