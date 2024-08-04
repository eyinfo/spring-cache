package com.eyinfo.springcache.storage;

import com.eyinfo.foundation.enums.Environment;
import com.eyinfo.foundation.utils.JsonUtils;
import com.eyinfo.springcache.mongo.MongoManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FastStorage {

    private static FastStorage fastStorage;

    public static FastStorage getInstance() {
        if (fastStorage == null) {
            synchronized (FastStorage.class) {
                if (fastStorage == null) {
                    fastStorage = new FastStorage();
                }
            }
        }
        return fastStorage;
    }

    private final StorageConfiguration configuration;

    private FastStorage() {
        configuration = StorageUtils.getConfiguration();
    }

    private final Map<String, Object> tempMap = new LinkedHashMap<>();

    /**
     * 设置缓存数据
     *
     * @param key    存储key
     * @param value  存储数据
     * @param period 缓存时间（毫秒）,0-永久存储
     * @param <T>    value数据类型
     */
    public <T> void set(String key, T value, long period) {
        MongoTemplate mongoTemplate = configuration.getMongoTemplate();
        Environment environment = Environment.getEnvironment(configuration.getActive());
        String content;
        if (value instanceof String || value instanceof Integer || value instanceof Double || value instanceof Float) {
            content = String.valueOf(value);
        } else {
            content = JsonUtils.toStr(value);
        }
        if (period == 0 || period > 30000) {
            MongoManager.getInstance().set(mongoTemplate, environment, key, content, period);
        } else {
            setRedisValue(key, value, period, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 设置缓存数据
     *
     * @param key   存储key
     * @param value 存储数据
     * @param <T>   value数据类型
     */
    public <T> void set(String key, T value) {
        this.set(key, value, 0);
    }

    private <T> T getMapValue(String key) {
        return (T) tempMap.get(key);
    }

    private Map.Entry<String, Object> getTail() {
        Iterator<Map.Entry<String, Object>> iterator = tempMap.entrySet().iterator();
        Map.Entry<String, Object> tail = null;
        while (iterator.hasNext()) {
            tail = iterator.next();
        }
        return tail;
    }

    private void setMapValue(String key, Object value) {
        if (tempMap.size() > 10000) {
            Map.Entry<String, Object> last = getTail();
            if (last != null) {
                tempMap.remove(last.getKey());
            }
        }
        tempMap.put(key, value);
    }

    private <T> T getRedisValue(String key) {
        RedisTemplate redisTemplate = configuration.getRedisTemplate();
        ValueOperations<String, T> opsForValue = redisTemplate.opsForValue();
        String active = configuration.getActive();
        return opsForValue.get(String.format("%s_%s", active, key));
    }

    private <T> void setRedisValue(String key, T value, long timeout, TimeUnit unit) {
        RedisTemplate redisTemplate = configuration.getRedisTemplate();
        String active = configuration.getActive();
        redisTemplate.opsForValue().set(String.format("%s_%s", active, key), value, timeout, unit);
    }

    private void removeRedis(String key) {
        RedisTemplate redisTemplate = configuration.getRedisTemplate();
        String active = configuration.getActive();
        redisTemplate.delete(String.format("%s_%s", active, key));
    }

    private <T, Item> T getMongoValue(String key, Class<Item> itemClass) {
        MongoTemplate mongoTemplate = configuration.getMongoTemplate();
        Environment environment = Environment.getEnvironment(configuration.getActive());
        String content = MongoManager.getInstance().getByKey(mongoTemplate, environment, key);
        if (itemClass == String.class || JsonUtils.isEmpty(content)) {
            return (T) content;
        }
        if (JsonUtils.isArray(content)) {
            return (T) JsonUtils.parseArray(content, itemClass);
        } else {
            return (T) JsonUtils.parseT(content, itemClass);
        }
    }

    private void removeMongo(String key) {
        MongoTemplate mongoTemplate = configuration.getMongoTemplate();
        Environment environment = Environment.getEnvironment(configuration.getActive());
        MongoManager.getInstance().deleteByKey(mongoTemplate, environment, key);
    }

    public <T, Item> T get(String key, Class<Item> itemClass) {
        T mapValue = getMapValue(key);
        if (mapValue != null) {
            return mapValue;
        }
        T redisValue = getRedisValue(key);
        if (redisValue != null) {
            setMapValue(key, redisValue);
            return redisValue;
        }
        T mongoValue = getMongoValue(key, itemClass);
        if (mongoValue != null) {
            setMapValue(key, mongoValue);
            setRedisValue(key, mongoValue, 30, TimeUnit.SECONDS);
            return mongoValue;
        }
        return null;
    }

    /**
     * 移除存储数据
     *
     * @param key 存储key
     */
    public void remove(String key) {
        tempMap.remove(key);
        removeRedis(key);
        removeMongo(key);
    }
}
