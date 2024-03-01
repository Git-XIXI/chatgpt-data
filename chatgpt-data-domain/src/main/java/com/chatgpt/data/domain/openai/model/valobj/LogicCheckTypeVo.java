package com.chatgpt.data.domain.openai.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 逻辑校验类型，值对象
 * @date 2024/2/29 23:55
 */
@Getter
@AllArgsConstructor
public enum LogicCheckTypeVo {

    SUCCESS("0000","校验通过"),
    REFUSE("0001","校验拒绝"),
            ;

    private final String code;
    private final String info;
}
