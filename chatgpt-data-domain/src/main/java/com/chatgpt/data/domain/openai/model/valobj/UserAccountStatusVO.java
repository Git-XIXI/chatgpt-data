package com.chatgpt.data.domain.openai.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 账号状态
 * @date 2024/3/1 23:52
 */
@Getter
@AllArgsConstructor
public enum UserAccountStatusVO {
    FREEZE(0, "冻结"),
    AVAILABLE(1, "可用"),
    ;
    private final Integer code;
    private final String info;

    public static UserAccountStatusVO get(Integer code) {
        switch (code) {
            case 0:
                return UserAccountStatusVO.AVAILABLE;
            case 1:
                return UserAccountStatusVO.FREEZE;
            default:
                return UserAccountStatusVO.AVAILABLE;
        }
    }
}
