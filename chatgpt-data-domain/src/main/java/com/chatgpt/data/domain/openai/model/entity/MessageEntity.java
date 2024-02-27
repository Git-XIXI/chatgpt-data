package com.chatgpt.data.domain.openai.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 消息体
 * @date 2024/1/18 0:14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {
    private String role;
    private String content;
}
