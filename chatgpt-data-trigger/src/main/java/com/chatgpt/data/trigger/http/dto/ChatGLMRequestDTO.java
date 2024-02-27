package com.chatgpt.data.trigger.http.dto;

import com.chatgpt.data.domain.openai.model.entity.MessageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: ChatGLMRequestDTO
 * @date 2024/1/22 22:43
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGLMRequestDTO {
    /**
     * 默认模型
     */
    private String model;

    /** 问题描述 */
    private List<MessageEntity> messages;
}

