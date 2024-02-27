package com.chatgpt.data.trigger.http;

import cn.bugstack.chatglm.model.Model;
import com.alibaba.fastjson.JSON;
import com.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.chatgpt.data.domain.openai.model.entity.MessageEntity;
import com.chatgpt.data.domain.openai.service.IChatService;
import com.chatgpt.data.trigger.http.dto.ChatGLMRequestDTO;
import com.chatgpt.data.types.exception.ChatGLMException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

/**
 * @description: ChatGLMAIServiceController
 * @date 2024/1/22 22:44
 */

@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/")
public class ChatGLMAIServiceController {
    @Resource
    private IChatService chatService;

    @RequestMapping(value = "chat/completions", method = RequestMethod.POST)
    public ResponseBodyEmitter completionStream(@RequestBody ChatGLMRequestDTO request,
                                                @RequestHeader("Authorization") String token,
                                                HttpServletResponse response) {

        log.info("流式问答请求开始，使用模型: {} 请求信息: {}", request.getModel(), JSON.toJSONString(request.getMessages()));
        try {
            // 1.基础配置；流式输出、编码、禁用缓存
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Cache-Control", "no-cache");

            // 2.构建参数
            ChatProcessAggregate chatProcessAggregate = ChatProcessAggregate.builder()
                    .token(token)
                    .model(Model.valueOf(request.getModel()))
                    .messages(request.getMessages().stream()
                            .map(entity -> MessageEntity.builder()
                                    .role(entity.getRole())
                                    .content(entity.getContent())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
            return chatService.completions(chatProcessAggregate);
        } catch (Exception e) {
            log.error("流式应答，请求模型：{} 发生异常", request.getModel(), e);
            throw new ChatGLMException(e.getMessage());
        }
    }

    @PostMapping("test")
    public void testController() {
        log.info("测试通过");
    }
}
