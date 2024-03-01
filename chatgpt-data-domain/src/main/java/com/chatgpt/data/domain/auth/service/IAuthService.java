package com.chatgpt.data.domain.auth.service;


import com.chatgpt.data.domain.auth.model.entity.AuthStateEntity;

/**
 * @description: 鉴权验证服务接口
 * @date 2024/2/27 21:29
 */
public interface IAuthService {

    /**
     * 登录验证
     * @param code 验证码
     * @return Token
     */
    AuthStateEntity doLogin(String code);

    boolean checkToken(String token);

    String getOpenId(String token);

}
