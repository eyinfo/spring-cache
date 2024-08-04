package com.eyinfo.springcache.storage;

import com.eyinfo.foundation.enums.Environment;
import com.eyinfo.foundation.utils.JsonUtils;
import com.eyinfo.springcache.mongo.MongoManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StorageUtils {

    static StorageConfiguration configuration;
    //数据最长缓存时间(毫秒)
    private static int maxCacheTime = 120000;

    public static StorageConfiguration getConfiguration() {
        return configuration;
    }

    public static void save(Object data, String objectUnique) {
        String content = JsonUtils.toStr(data);
        MongoTemplate mongoTemplate = configuration.getMongoTemplate();
        Environment environment = Environment.getEnvironment(configuration.getActive());
        MongoManager.getInstance().set(mongoTemplate, environment, objectUnique, content, maxCacheTime);
    }

    public static <R, Item> R get(String key, Class<Item> itemClass, boolean isList) {
        MongoTemplate mongoTemplate = configuration.getMongoTemplate();
        Environment environment = Environment.getEnvironment(configuration.getActive());
        String content = MongoManager.getInstance().getByKey(mongoTemplate, environment, key);
        if (itemClass == String.class || JsonUtils.isEmpty(content)) {
            return (R) content;
        }
        if (isList) {
            return (R) JsonUtils.parseArray(content, itemClass);
        } else {
            return (R) JsonUtils.parseT(content, itemClass);
        }
    }

    public static void cleanContainsPrefixCache(DbMethodEntry[] methodEntry) {
        MongoTemplate mongoTemplate = configuration.getMongoTemplate();
        Environment environment = Environment.getEnvironment(configuration.getActive());
        List<String> keys = Arrays.stream(methodEntry).map(MethodEntry::getCacheSubKey).collect(Collectors.toList());
        MongoManager.getInstance().blurDelete(mongoTemplate, environment, keys);
    }

    public static void deleteCache(String key) {
        MongoTemplate mongoTemplate = configuration.getMongoTemplate();
        Environment environment = Environment.getEnvironment(configuration.getActive());
        MongoManager.getInstance().deleteByKey(mongoTemplate, environment, key);
    }

    public static void saveRedis(String key, Object data) {
        RedisTemplate redisTemplate = configuration.getRedisTemplate();
        redisTemplate.opsForValue().set(key, data, 2, TimeUnit.DAYS);
    }

    public static <R> R getFromRedis(String key) {
        RedisTemplate redisTemplate = configuration.getRedisTemplate();
        ValueOperations<String, R> opsForValue = redisTemplate.opsForValue();
        return opsForValue.get(key);
    }
}
