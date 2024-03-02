package com.chatgpt.data.infrastructure.repository;

import com.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;
import com.chatgpt.data.domain.openai.model.valobj.UserAccountStatusVO;
import com.chatgpt.data.domain.openai.repository.IOpenAiRepository;
import com.chatgpt.data.infrastructure.dao.IUserAccountDao;
import com.chatgpt.data.infrastructure.po.UserAccountPO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @description: OpenaAi 仓储接口
 * @date 2024/3/2 0:18
 */
@Repository
public class OpenAiRepository implements IOpenAiRepository {
    @Resource
    private IUserAccountDao userAccountDao;

    @Override
    public int subAccountQuota(String openId) {
        return userAccountDao.subAccountQuota(openId);
    }

    @Override
    public UserAccountQuotaEntity queryUserAccount(String openId) {
        UserAccountPO userAccountPO = userAccountDao.queryUserAccount(openId);
        if (null == userAccountPO) return null;
        UserAccountQuotaEntity userAccountQuotaEntity = UserAccountQuotaEntity.builder()
                .openId(userAccountPO.getOpenId())
                .totalQuota(userAccountPO.getTotalQuota())
                .surplusQuota(userAccountPO.getSurplusQuota())
                .userAccountStatusVO(UserAccountStatusVO.get(userAccountPO.getStatus()))
                .build();
        userAccountQuotaEntity.genModelTypes(userAccountPO.getModelTypes());
        return userAccountQuotaEntity;
    }
}
