package com.chatgpt.data.domain.openai.service;

import com.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;
import com.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import com.chatgpt.data.domain.openai.repository.IOpenAiRepository;
import com.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import com.chatgpt.data.types.common.Constants;
import com.chatgpt.data.types.exception.ChatGLMException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @description: AbstractChatService
 * @date 2024/1/25 23:18
 */
@Slf4j
public abstract class AbstractChatService implements IChatService {
    @Resource
    private IOpenAiRepository openAiRepository;
    @Override
    public ResponseBodyEmitter completions(ResponseBodyEmitter emitter, ChatProcessAggregate chatProcess) {
        try {
            // 1.请求响应
            emitter.onCompletion(() -> log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel()));

            emitter.onError(throwable -> log.error("流式问答请求异常，使用模型：{}", chatProcess.getModel(), throwable));

            // 2.获取账户
            UserAccountQuotaEntity userAccountQuotaEntity = openAiRepository.queryUserAccount(chatProcess.getOpenId());
            log.info("用户账户信息：{}", userAccountQuotaEntity);

            // 3.规则过滤
            RuleLogicEntity<ChatProcessAggregate> ruleLogicEntity = this.doCheckLogic(chatProcess, userAccountQuotaEntity,
                    DefaultLogicFactory.LogicModelEnum.ACCESS_LIMIT.getCode(),
                    DefaultLogicFactory.LogicModelEnum.SENSITIVE_WORD.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModelEnum.ACCOUNT_STATUS.getCode() : DefaultLogicFactory.LogicModelEnum.NULL.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModelEnum.MODEL_TYPE.getCode() : DefaultLogicFactory.LogicModelEnum.NULL.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModelEnum.USER_QUOTA.getCode() : DefaultLogicFactory.LogicModelEnum.NULL.getCode());

            if (LogicCheckTypeVO.REFUSE.equals(ruleLogicEntity.getType())) {
                emitter.send(ruleLogicEntity.getInfo());
                emitter.complete();
                return emitter;
            }

            // 4.应答处理
            this.doMessageResponse(ruleLogicEntity.getData(), emitter);
        } catch (Exception e) {
            throw new ChatGLMException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }

        // 3.返回结果
        return emitter;
    }

    protected abstract RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcessAggregate, UserAccountQuotaEntity userAccountQuotaEntity, String... logics) throws ExecutionException;

    protected abstract void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws Exception;
}
