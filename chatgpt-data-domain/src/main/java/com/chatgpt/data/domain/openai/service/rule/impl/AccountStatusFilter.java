
package com.chatgpt.data.domain.openai.service.rule.impl;

import com.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.chatgpt.data.domain.openai.model.annotation.LogicStrategy;
import com.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;
import com.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import com.chatgpt.data.domain.openai.model.valobj.UserAccountStatusVO;
import com.chatgpt.data.domain.openai.service.rule.ILogicFilter;
import com.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: 账户状态校验
 * @date 2024/3/2 13:25
 */

@Slf4j
@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModelEnum.ACCOUNT_STATUS)
public class AccountStatusFilter implements ILogicFilter<UserAccountQuotaEntity> {
    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcessAggregate, UserAccountQuotaEntity data) {
        log.info("开始校验账户状态；openId：{} 账号状态：{}", chatProcessAggregate.getOpenId(), data.getUserAccountStatusVO().getInfo());
        // 账户可用，直接放行
        if (UserAccountStatusVO.AVAILABLE.equals(data.getUserAccountStatusVO())) {
            log.info("账户可用，放行 openId：{}", chatProcessAggregate.getOpenId());
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS)
                    .data(chatProcessAggregate)
                    .build();
        }
        log.info("账户冻结，拒绝访问；openId：{}", chatProcessAggregate.getOpenId());
        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .info("您的账户已冻结，暂时不可使用。如有疑问，可联系客服解冻账户。")
                .type(LogicCheckTypeVO.REFUSE)
                .data(chatProcessAggregate)
                .build();
    }
}
