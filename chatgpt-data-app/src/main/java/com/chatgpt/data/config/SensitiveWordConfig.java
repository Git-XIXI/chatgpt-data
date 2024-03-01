package com.chatgpt.data.config;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.utils.InnerWordCharUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 敏感词配置
 * @date 2024/2/29 23:08
 */
@Slf4j
@Configuration
public class SensitiveWordConfig {
    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        return SensitiveWordBs.newInstance()
                /*
                  替换敏感词的回调函数
                  @param stringBuilder StringBuilder对象
                  @param chars 字符数组
                  @param wordResult 敏感词匹配结果
                  @param iWordContext 敏感词上下文
                 */
                .wordReplace((stringBuilder, chars, wordResult, iWordContext) -> {
                    String sensitiveWord = InnerWordCharUtils.getString(chars, wordResult);
                    log.info("检测到敏感词：{}", sensitiveWord);
                })
                .ignoreCase(true)
                .ignoreWidth(true)
                .ignoreNumStyle(true)
                .ignoreChineseStyle(true)
                .ignoreEnglishStyle(true)
                .ignoreRepeat(false)
                .enableNumCheck(true)
                .enableEmailCheck(true)
                .enableUrlCheck(true)
                .enableWordCheck(true)
                .numCheckLen(1024)
                .init();
    }
}
