package com.chatgpt.data.domain.openai.service;

import cn.bugstack.chatglm.model.ChatCompletionRequest;
import cn.bugstack.chatglm.model.ChatCompletionResponse;
import cn.bugstack.chatglm.session.OpenAiSession;
import com.alibaba.fastjson.JSON;
import com.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVo;
import com.chatgpt.data.domain.openai.service.rule.ILogicFilter;
import com.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @description: ChatService
 * @date 2024/1/29 22:38
 */
@Service
@Slf4j
public class ChatService extends AbstractChatService {
    @Resource
    private DefaultLogicFactory logicFactory;

    @Resource
    private OpenAiSession openAiSession;

    @Override
    protected RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcessAggregate, String... logics) throws ExecutionException {
        Map<String, ILogicFilter> logicFilterMap = logicFactory.openLogicFilter();
        RuleLogicEntity<ChatProcessAggregate> entity = null;
        for (String code : logics) {
            entity = logicFilterMap.get(code).filter(chatProcessAggregate);
            if (LogicCheckTypeVo.REFUSE.equals(entity.getType())) return entity;
        }
        return entity != null ? entity :
                RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVo.SUCCESS)
                .data(chatProcessAggregate)
                .build();
    }

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
