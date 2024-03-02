package com.chatgpt.data.domain.openai.repository;

import com.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;

/**
 * @description: OpenAi 仓储接口
 * @date 2024/3/1 23:15
 */
public interface IOpenAiRepository {
    int subAccountQuota(String openId);

    UserAccountQuotaEntity queryUserAccount(String openId);
}
