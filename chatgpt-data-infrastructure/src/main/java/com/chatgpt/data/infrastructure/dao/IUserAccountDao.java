package com.chatgpt.data.infrastructure.dao;

import com.chatgpt.data.infrastructure.po.UserAccountPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserAccountDao {
    int subAccountQuota(String openId);

    UserAccountPO queryUserAccount(String openId);

    void insert(UserAccountPO userAccountPO);

    int addAccountQuota(UserAccountPO userAccountPO);
}
