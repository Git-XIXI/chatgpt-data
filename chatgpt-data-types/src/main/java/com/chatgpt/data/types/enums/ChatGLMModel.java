package com.chatgpt.data.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 模型对象
 * @date 2024/1/19 0:18
 */
@Getter
@AllArgsConstructor
public enum ChatGLMModel {
    CHATGLM_LITE("chatglm_lite"),

    ;
    private final String code;
}
