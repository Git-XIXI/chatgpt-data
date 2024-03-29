package com.chatgpt.data.domain.openai.service;

import com.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.concurrent.ExecutionException;

/**
 * @description: IChatService
 * @date 2024/1/25 23:19
 */
public interface IChatService {
    ResponseBodyEmitter completions(ResponseBodyEmitter emitter, ChatProcessAggregate chatProcess) throws ExecutionException;
}
