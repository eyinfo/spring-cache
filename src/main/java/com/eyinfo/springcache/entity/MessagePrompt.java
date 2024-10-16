package com.eyinfo.springcache.entity;

import com.eyinfo.foundation.entity.BaseResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "messages")
public class MessagePrompt {

    // message definition collection
    private Map<String, BaseResponse> prompt;

    public Map<String, BaseResponse> getPrompt() {
        return prompt;
    }

    public void setPrompt(Map<String, BaseResponse> prompt) {
        this.prompt = prompt;
    }
}
