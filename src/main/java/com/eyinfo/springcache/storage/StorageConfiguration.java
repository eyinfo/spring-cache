package com.eyinfo.springcache.storage;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@ConfigurationProperties(prefix = "spring.profiles")
public class StorageConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private String active;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        StorageUtils.configuration = this;
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

    public RedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }
}
