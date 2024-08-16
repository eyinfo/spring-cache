package com.eyinfo.springcache.storage;

import com.eyinfo.foundation.utils.JsonUtils;
import com.eyinfo.springcache.mongo.MongoManager;
import com.eyinfo.springcache.redis.RedisManager;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SecondaryStorage {

    private static SecondaryStorage secondaryStorage;

    public static SecondaryStorage getInstance() {
        if (secondaryStorage == null) {
            synchronized (SecondaryStorage.class) {
                if (secondaryStorage == null) {
                    secondaryStorage = new SecondaryStorage();
                }
            }
        }
        return secondaryStorage;
    }


    private SecondaryStorage() {

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
        String content;
        if (value instanceof String || value instanceof Integer || value instanceof Double || value instanceof Float) {
            content = String.valueOf(value);
        } else {
            content = JsonUtils.toStr(value);
        }
        if (period == 0 || period > 30000) {
            MongoManager.getInstance().set(key, content, period);
        } else {
            RedisManager.getInstance().set(key, value, period, TimeUnit.MILLISECONDS);
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

    private <T, Item> T getMongoValue(String key, Class<Item> itemClass) {
        String content = MongoManager.getInstance().getByKey(key);
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
        MongoManager.getInstance().deleteByKey(key);
    }

    public <T, Item> T get(String key, Class<Item> itemClass) {
        T mapValue = getMapValue(key);
        if (mapValue != null) {
            return mapValue;
        }
        T redisValue = RedisManager.getInstance().get(key);
        if (redisValue != null) {
            setMapValue(key, redisValue);
            return redisValue;
        }
        T mongoValue = getMongoValue(key, itemClass);
        if (mongoValue != null) {
            setMapValue(key, mongoValue);
            RedisManager.getInstance().set(key, mongoValue, 30, TimeUnit.SECONDS);
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
        RedisManager.getInstance().remove(key);
        removeMongo(key);
    }
}
