package com.eyinfo.springcache.storage;

import com.eyinfo.foundation.entity.Result;
import com.eyinfo.springcache.entity.CachingStrategyConfig;
import com.eyinfo.springcache.entity.MessagePrompt;
import jakarta.annotation.Resource;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.profiles")
public class StorageConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private String active;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CachingStrategyConfig cachingStrategyConfig;

    @Resource
    private MessagePrompt messagePrompt;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        StorageUtils.configuration = this;
        Result.setMessageConfig(this.messagePrompt.getPrompt());
    }

    public String getActive() {
        if (this.active == null) {
            this.active = this.applicationContext.getEnvironment().getProperty("spring.profiles.active");
        }
        return this.active;
    }

    public MongoTemplate getMongoTemplate() {
        return this.mongoTemplate;
    }

    public <K, V> RedisTemplate<K, V> getRedisTemplate() {
        return this.redisTemplate;
    }

    public CachingStrategyConfig getCachingStrategyConfig() {
        return cachingStrategyConfig;
    }
}
