package com.chatgpt.data.domain.weixin.service.validate;

import com.chatgpt.data.domain.weixin.service.IWeiXinValidateService;
import com.chatgpt.data.types.weixin.SignatureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @description: 验签接口实现
 * @date 2024/2/27 21:29
 */
@Service
public class WeiXinValidateService implements IWeiXinValidateService {

    @Value("${wx.config.token}")
    private String token;

    @Override
    public boolean checkSign(String signature, String timestamp, String nonce) {
        return SignatureUtil.check(token, signature, timestamp, nonce);
    }

}
