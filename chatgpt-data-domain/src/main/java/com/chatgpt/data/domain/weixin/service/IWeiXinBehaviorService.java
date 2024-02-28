package com.chatgpt.data.domain.weixin.service;


import com.chatgpt.data.domain.weixin.model.entity.UserBehaviorMessageEntity;

/**
 * @description: 受理用户行为接口
 * @date 2024/2/27 21:29
 */
public interface IWeiXinBehaviorService {

    String acceptUserBehavior(UserBehaviorMessageEntity userBehaviorMessageEntity);

}
