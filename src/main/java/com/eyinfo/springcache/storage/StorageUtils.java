package com.eyinfo.springcache.storage;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

public class StorageUtils {

    static StorageConfiguration configuration;

    public static StorageConfiguration getConfiguration() {
        return configuration;
    }

    public static void saveRedis(String key, Object data) {
        RedisTemplate redisTemplate = configuration.getRedisTemplate();
        String active = configuration.getActive();
        redisTemplate.opsForValue().set(String.format("%s_%s", active, key), data, 2, TimeUnit.DAYS);
    }

    public static <R> R getFromRedis(String key) {
        RedisTemplate redisTemplate = configuration.getRedisTemplate();
        ValueOperations<String, R> opsForValue = redisTemplate.opsForValue();
        String active = configuration.getActive();
        return opsForValue.get(String.format("%s_%s", active, key));
    }
}
