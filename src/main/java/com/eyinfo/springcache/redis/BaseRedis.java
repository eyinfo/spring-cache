package com.eyinfo.springcache.redis;

import com.eyinfo.foundation.enums.Environment;
import com.eyinfo.springcache.entity.CachingStrategyConfig;
import com.eyinfo.springcache.storage.StorageConfiguration;
import com.eyinfo.springcache.storage.StorageUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

abstract class BaseRedis {


    /**
     * 获取redis模板
     */
    public <K, V> RedisTemplate<K, V> getRedisTemplate() {
        StorageConfiguration configuration = StorageUtils.getConfiguration();
        if (configuration == null) {
            return null;
        }
        return configuration.getRedisTemplate();
    }

    /**
     * 数据缓存
     *
     * @param environment 环境
     * @param key         缓存key
     * @param content     缓存数据
     * @param period      缓存时间段
     * @param unit        缓存时间单位
     */
    <K, V> void set(Environment environment, String key, V content, long period, TimeUnit unit) {
        RedisTemplate<String, V> redisTemplate = getRedisTemplate();
        if (redisTemplate == null) {
            return;
        }
        String cacheKey = String.format("%s_%s", environment.name(), key);
        if (unit == null) {
            CachingStrategyConfig strategyConfig = StorageUtils.getCachingStrategyConfig();
            Long redisGlobalCacheTime = strategyConfig.getRedisGlobalCacheTime();
            if (redisGlobalCacheTime == null) {
                redisTemplate.opsForValue().set(cacheKey, content);
            } else {
                redisTemplate.opsForValue().set(cacheKey, content, redisGlobalCacheTime, TimeUnit.MILLISECONDS);
            }
        } else {
            redisTemplate.opsForValue().set(cacheKey, content, period, unit);
        }
    }

    /**
     * 数据缓存
     *
     * @param environment 环境
     * @param key         缓存key
     * @param content     缓存数据
     */
    <V> void set(Environment environment, String key, V content) {
        this.set(environment, key, content, 0, null);
    }

    /**
     * 删除缓存数据
     *
     * @param environment 环境
     * @param key         缓存key
     */
    void remove(Environment environment, String key) {
        RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
        if (redisTemplate == null) {
            return;
        }
        String cacheKey = String.format("%s_%s", environment.name(), key);
        redisTemplate.delete(cacheKey);
    }

    /**
     * 批量删除缓存数据
     *
     * @param environment 环境
     * @param keys        缓存key集合
     */
    void removes(Environment environment, Set<String> keys) {
        RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
        if (redisTemplate == null) {
            return;
        }
        Set<String> cacheKeys = keys.stream().map(m -> String.format("%s_%s", environment.name(), m)).collect(Collectors.toSet());
        redisTemplate.delete(cacheKeys);
    }

    /**
     * 模糊删除缓存数据
     *
     * @param environment 环境
     * @param containKey  缓存key包含的关键字
     */
    void blurRemove(Environment environment, String containKey) {
        RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
        if (redisTemplate == null) {
            return;
        }
        Set<String> keys = redisTemplate.keys(containKey);
        Set<String> cacheKeys = keys.stream().filter(m -> m.startsWith(environment.name())).collect(Collectors.toSet());
        redisTemplate.delete(cacheKeys);
    }

    /**
     * 获取缓存数据
     *
     * @param environment 环境
     * @param key         缓存记录key
     * @return 返回缓存数据
     */
    <R> R get(Environment environment, String key) {
        RedisTemplate<String, R> redisTemplate = getRedisTemplate();
        if (redisTemplate == null) {
            return null;
        }
        String cacheKey = String.format("%s_%s", environment.name(), key);
        ValueOperations<String, R> opsForValue = redisTemplate.opsForValue();
        return opsForValue.get(cacheKey);
    }
}
