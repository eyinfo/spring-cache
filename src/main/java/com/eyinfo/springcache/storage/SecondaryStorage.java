package com.eyinfo.springcache.storage;

import com.eyinfo.foundation.utils.JsonUtils;
import com.eyinfo.springcache.mongo.MongoManager;

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

    /**
     * 设置缓存数据
     *
     * @param key    存储key
     * @param value  存储数据
     * @param period 缓存时间（毫秒）
     * @param <T>    value数据类型
     */
    public <T> void set(String key, T value, long period) {
        String content;
        if (value instanceof String || value instanceof Integer || value instanceof Double || value instanceof Float) {
            content = String.valueOf(value);
        } else {
            content = JsonUtils.toStr(value);
        }
        MongoManager.getInstance().set(key, content, period);
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
        return getMongoValue(key, itemClass);
    }

    /**
     * 移除存储数据
     *
     * @param key 存储key
     */
    public void remove(String key) {
        removeMongo(key);
    }
}
