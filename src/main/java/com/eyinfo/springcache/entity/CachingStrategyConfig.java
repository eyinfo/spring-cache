package com.eyinfo.springcache.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.caching.strategy")
public class CachingStrategyConfig {
    //api全局缓存时间(毫秒)
    private Long apiGlobalCacheTime = 120000L;

    //redis全局缓存时间(毫秒)
    private Long redisGlobalCacheTime = 60000L;

    //mongo全局缓存时间(毫秒)
    private Long mongoGlobalCacheTime = 300000L;

    public Long getApiGlobalCacheTime() {
        return apiGlobalCacheTime;
    }

    public void setApiGlobalCacheTime(Long apiCacheTime) {
        this.apiGlobalCacheTime = apiCacheTime;
    }

    public Long getRedisGlobalCacheTime() {
        return redisGlobalCacheTime;
    }

    public void setRedisGlobalCacheTime(Long redisGlobalCacheTime) {
        this.redisGlobalCacheTime = redisGlobalCacheTime;
    }

    public Long getMongoGlobalCacheTime() {
        return mongoGlobalCacheTime;
    }

    public void setMongoGlobalCacheTime(Long mongoGlobalCacheTime) {
        this.mongoGlobalCacheTime = mongoGlobalCacheTime;
    }
}
