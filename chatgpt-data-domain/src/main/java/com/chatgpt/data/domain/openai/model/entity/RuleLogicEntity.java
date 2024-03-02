package com.chatgpt.data.domain.openai.model.entity;

import com.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 规则检验结果实体
 * @date 2024/2/29 23:51
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleLogicEntity<T> {
    private LogicCheckTypeVO type;
    private String info;
    private T data;

}
