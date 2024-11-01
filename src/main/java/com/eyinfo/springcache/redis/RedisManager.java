package com.eyinfo.springcache.redis;

import com.eyinfo.foundation.enums.Environment;
import com.eyinfo.springcache.storage.StorageUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020/7/2
 * Description:https://yq.aliyun.com/articles/645266/
 * Modifier:
 * ModifyContent:
 */
public class RedisManager extends BaseRedis {

    private static RedisManager redisManager;

    private RedisManager() {

    }

    public static RedisManager getInstance() {
        if (redisManager == null) {
            synchronized (RedisManager.class) {
                if (redisManager == null) {
                    redisManager = new RedisManager();
                }
            }
        }
        return redisManager;
    }

    /**
     * 数据缓存
     *
     * @param key     缓存key
     * @param content 缓存数据
     * @param period  缓存时间段
     * @param unit    缓存时间单位
     */
    public <K, V> void set(String key, V content, long period, TimeUnit unit) {
        Environment environment = StorageUtils.getEnvironment();
        super.set(environment, key, content, period, unit);
    }

    /**
     * 数据缓存
     *
     * @param key     缓存key
     * @param content 缓存数据
     */
    public <V> void set(String key, V content) {
        Environment environment = StorageUtils.getEnvironment();
        super.set(environment, key, content);
    }

    /**
     * 删除缓存数据
     *
     * @param key 缓存key
     */
    public void remove(String key) {
        Environment environment = StorageUtils.getEnvironment();
        super.remove(environment, key);
    }

    /**
     * 批量删除缓存数据
     *
     * @param keys 缓存key集合
     */
    public void removes(Set<String> keys) {
        Environment environment = StorageUtils.getEnvironment();
        super.removes(environment, keys);
    }

    /**
     * 模糊删除缓存数据
     *
     * @param containKey 缓存key包含的关键字
     */
    public void blurRemove(String containKey) {
        Environment environment = StorageUtils.getEnvironment();
        super.blurRemove(environment, containKey);
    }

    /**
     * 获取缓存数据
     *
     * @param key 缓存记录key
     * @return 返回缓存数据
     */
    public <R> R get(String key) {
        Environment environment = StorageUtils.getEnvironment();
        return super.get(environment, key);
    }

    public <K> Long increment(K key) {
        Environment environment = StorageUtils.getEnvironment();
        String cacheKey = String.format("%s_%s", environment.name(), key);
        RedisTemplate<Object, Object> redisTemplate = super.getRedisTemplate();
        return redisTemplate.opsForValue().increment(cacheKey);
    }

    public <K> Long increment(K key, long delta) {
        Environment environment = StorageUtils.getEnvironment();
        String cacheKey = String.format("%s_%s", environment.name(), key);
        RedisTemplate<Object, Object> redisTemplate = super.getRedisTemplate();
        return redisTemplate.opsForValue().increment(cacheKey, delta);
    }

    public <K> Double increment(K key, double delta) {
        Environment environment = StorageUtils.getEnvironment();
        String cacheKey = String.format("%s_%s", environment.name(), key);
        RedisTemplate<Object, Object> redisTemplate = super.getRedisTemplate();
        return redisTemplate.opsForValue().increment(cacheKey, delta);
    }

    public <K> Long decrement(K key) {
        Environment environment = StorageUtils.getEnvironment();
        String cacheKey = String.format("%s_%s", environment.name(), key);
        RedisTemplate<Object, Object> redisTemplate = super.getRedisTemplate();
        return redisTemplate.opsForValue().decrement(cacheKey);
    }

    public <K> Long decrement(K key, long delta) {
        Environment environment = StorageUtils.getEnvironment();
        String cacheKey = String.format("%s_%s", environment.name(), key);
        RedisTemplate<Object, Object> redisTemplate = super.getRedisTemplate();
        return redisTemplate.opsForValue().decrement(cacheKey, delta);
    }

    public <K, V> Boolean setIfPresent(K key, V value) {
        Environment environment = StorageUtils.getEnvironment();
        String cacheKey = String.format("%s_%s", environment.name(), key);
        return super.getRedisTemplate().opsForValue().setIfPresent(cacheKey, value);
    }

    public <K, V> Boolean setIfPresent(K key, V value, long timeout, TimeUnit unit) {
        Environment environment = StorageUtils.getEnvironment();
        String cacheKey = String.format("%s_%s", environment.name(), key);
        return super.getRedisTemplate().opsForValue().setIfPresent(cacheKey, value, timeout, unit);
    }
}
