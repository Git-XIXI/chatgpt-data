package com.chatgpt.data.domain.openai.service;

import com.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVo;
import com.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import com.chatgpt.data.types.common.Constants;
import com.chatgpt.data.types.exception.ChatGLMException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.concurrent.ExecutionException;

/**
 * @description: AbstractChatService
 * @date 2024/1/25 23:18
 */
@Slf4j
public abstract class AbstractChatService implements IChatService {
    @Override
    public ResponseBodyEmitter completions(ResponseBodyEmitter emitter, ChatProcessAggregate chatProcess) {
        try {
            // 1.请求响应
            emitter.onCompletion(() -> log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel()));

            emitter.onError(throwable -> log.error("流式问答请求异常，使用模型：{}", chatProcess.getModel(), throwable));

            // 2.规则过滤
            RuleLogicEntity<ChatProcessAggregate> ruleLogicEntity = this.doCheckLogic(chatProcess,
                    DefaultLogicFactory.LogicModelEnum.ACCESS_LIMIT.getCode(),
                    DefaultLogicFactory.LogicModelEnum.SENSITIVE_WORD.getCode());

            if (LogicCheckTypeVo.REFUSE.equals(ruleLogicEntity.getType())) {
                emitter.send(ruleLogicEntity.getInfo());
                emitter.complete();
                return emitter;
            }

            // 3.应答处理
            this.doMessageResponse(ruleLogicEntity.getData(), emitter);
        } catch (Exception e) {
            throw new ChatGLMException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }

        // 3.返回结果
        return emitter;
    }

    protected abstract RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcessAggregate, String... logics) throws ExecutionException;

    protected abstract void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws Exception;
}
