package com.chatgpt.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: ChatGLM配置参数
 * @date 2023/11/21 23:08
 */
@Data
@ConfigurationProperties(prefix = "chatglm.sdk.config", ignoreInvalidFields = true)
public class ChatGLMSDKConfigProperties {

    /**
     * 状态；open = 开启、close 关闭
     */
    private boolean enable;
    /**
     * 转发地址
     */
    private String apiHost;
    /**
     * 可以申请 sk-***
     */
    private String apiSecretKey;

}
