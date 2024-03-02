package com.chatgpt.data.trigger.http;


import com.alibaba.fastjson.JSON;
import com.chatgpt.data.domain.auth.model.entity.AuthStateEntity;
import com.chatgpt.data.domain.auth.model.valobj.AuthTypeVO;
import com.chatgpt.data.domain.auth.service.IAuthService;
import com.chatgpt.data.domain.weixin.model.entity.MessageTextEntity;
import com.chatgpt.data.domain.weixin.model.entity.UserBehaviorMessageEntity;
import com.chatgpt.data.domain.weixin.model.valobj.MsgTypeVO;
import com.chatgpt.data.domain.weixin.service.IWeiXinBehaviorService;
import com.chatgpt.data.types.common.Constants;
import com.chatgpt.data.types.model.Response;
import com.chatgpt.data.types.weixin.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @description: 鉴权登录
 * @date 2024/2/27 23:28
 */

@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/auth/")
public class AuthContorller {

    @Resource
    private IAuthService authService;
    @Resource
    private IWeiXinBehaviorService weiXinBehaviorService;


    @PostMapping("login")
    public Response<String> doLogin(@RequestParam String code) {
        log.info("鉴权登录校验开始，验证码: {}", code);
        try {
            AuthStateEntity authStateEntity = authService.doLogin(code);
            log.info("鉴权登录校验完成，验证码: {} 结果: {}", code, JSON.toJSONString(authStateEntity));
            // 拦截，鉴权失败
            if (!AuthTypeVO.A0000.getCode().equals(authStateEntity.getCode())) {
                return Response.<String>builder()
                        .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                        .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                        .build();
            }

            // 放行，鉴权成功
            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(authStateEntity.getToken())
                    .build();
        } catch (Exception e) {
            log.error("鉴权登录校验失败，验证码: {}", code);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 生成验证码，用于测试使用
     * curl -X POST \
     * http://localhost:8080/api/auth/gen/code \
     * -H 'Content-Type: application/x-www-form-urlencoded' \
     * -d 'openid=oxfA9w8-23yvwTmo2ombz0E4zJv4'
     */
    @PostMapping("gen/code")
    public Response<String> genCode(@RequestParam String openid) {
        log.info("生成验证码开始，用户ID: {}", openid);
        try {
            UserBehaviorMessageEntity userBehaviorMessageEntity = new UserBehaviorMessageEntity();
            userBehaviorMessageEntity.setOpenId(openid);
            userBehaviorMessageEntity.setMsgType(MsgTypeVO.TEXT.getCode());
            userBehaviorMessageEntity.setContent("405");
            String xml = weiXinBehaviorService.acceptUserBehavior(userBehaviorMessageEntity);
            MessageTextEntity messageTextEntity = XmlUtil.xmlToBean(xml, MessageTextEntity.class);
            log.info("生成验证码完成，用户ID: {} 生成结果：{}", openid, messageTextEntity.getContent());
            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(messageTextEntity.getContent())
                    .build();
        } catch (Exception e) {
            log.info("生成验证码失败，用户ID: {}", openid);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                    .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                    .build();
        }
    }
}

