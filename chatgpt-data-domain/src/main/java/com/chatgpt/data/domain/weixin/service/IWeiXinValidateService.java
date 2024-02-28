package com.chatgpt.data.domain.weixin.service;

/**
 * @description: 验签接口
 * @date 2024/2/27 21:29
 */
public interface IWeiXinValidateService {

    boolean checkSign(String signature, String timestamp, String nonce);

}
