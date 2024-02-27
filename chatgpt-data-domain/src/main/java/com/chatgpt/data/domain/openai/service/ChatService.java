package com.chatgpt.data.domain.openai.service;

import cn.bugstack.chatglm.model.ChatCompletionRequest;
import cn.bugstack.chatglm.model.ChatCompletionResponse;
import cn.bugstack.chatglm.session.OpenAiSession;
import com.alibaba.fastjson.JSON;
import com.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: ChatService
 * @date 2024/1/29 22:38
 */
@Service
@Slf4j
public class ChatService extends AbstractChatService {

    @Autowired(required = false)
    private OpenAiSession openAiSession;

    @Override
    protected void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws Exception {
        // 入参：模型、请求信息
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(chatProcess.getModel());
        request.setIncremental(false);

        List<ChatCompletionRequest.Prompt> messages = chatProcess.getMessages().stream()
                .map(entity -> ChatCompletionRequest.Prompt.builder()
                        .role(entity.getRole())
                        .content(entity.getContent())
                        .build())
                .collect(Collectors.toList());
        request.setMessages(messages);

        // 请求
        openAiSession.completions(request, new EventSourceListener() {
            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {

                ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
                List<ChatCompletionResponse.Choice> choices = response.getChoices();
                for (ChatCompletionResponse.Choice choice : choices) {
                    // 应答完成
                    String finishReason = choice.getFinishReason();
                    if (StringUtils.isNoneBlank(finishReason) && "stop".equals(finishReason)) {
                        emitter.complete();
                        break;
                    }

                    // 发送消息
                    ChatCompletionResponse.Delta delta = choice.getDelta();
                    try {
                        emitter.send(delta.getContent());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }
}
