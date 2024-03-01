
package com.chatgpt.data.domain.openai.model.aggregates;

import cn.bugstack.chatglm.model.Model;
import com.chatgpt.data.domain.openai.model.entity.MessageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 对接gpt参数对象
 * @date 2024/1/17 23:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatProcessAggregate {

    /**
     * 用户ID
     */
    private String openId;
    /**
     * 模型
     */
    private Model model;
    /**
     * 问题描述
     */
    private List<MessageEntity> messages;

    public boolean isWhiteList(String whiteListStr) {
        String[] whiteList = whiteListStr.split(",");
        for (String whiteOpenId : whiteList) {
            if (whiteOpenId.equals(openId)) return true;
        }
        return false;
    }

}
