package com.eyinfo.springcache.mongo;

import com.eyinfo.foundation.enums.Environment;
import com.eyinfo.foundation.utils.ConvertUtils;
import com.eyinfo.foundation.utils.JsonUtils;
import com.eyinfo.foundation.utils.ObjectJudge;
import com.eyinfo.springcache.storage.DbMethodEntry;
import com.eyinfo.springcache.storage.MethodEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MongoManager extends BaseMongo {

    private static MongoManager mongoManager;

    public static MongoManager getInstance() {
        if (mongoManager == null) {
            synchronized (MongoManager.class) {
                if (mongoManager == null) {
                    mongoManager = new MongoManager();
                }
            }
        }
        return mongoManager;
    }

    @Override
    protected String getCollectionName(Environment environment) {
        return String.format("tbl_cache_record_%s", environment.name());
    }

    //数据最长缓存时间(毫秒)
    private final int maxCacheTime = 120000;

    /**
     * 根据dev、test、pre、prod相应环境缓存数据至mongodb
     *
     * @param key  缓存key
     * @param data 待缓存数据
     * @param <T>  缓存数据类型，支持：String、Integer、Double、Float以及可被序列化的对象
     */
    public <T> void save(String key, T data) {
        if (data instanceof Byte) {
            return;
        }
        String content;
        if (data instanceof String) {
            content = ConvertUtils.toString(data);
        } else if (data instanceof Integer ||
                data instanceof Double ||
                data instanceof Float) {
            content = String.valueOf(data);
        } else if (ObjectJudge.isSerializable(data)) {
            content = JsonUtils.toStr(data);
        } else {
            return;
        }
        this.set(key, content, maxCacheTime);
    }

    /**
     * 获取dev、test、pre、prod对应环境的mongo缓存数据
     *
     * @param key       缓存key
     * @param itemClass 序列化数据的class
     * @param isList    返回的数据是否为List
     * @param <R>       返回的数据类型
     * @param <Item>    返回数据的单个实体类型，若为对象Item与R类型一致
     * @return R类型的数据
     */
    public <R, Item> R get(String key, Class<Item> itemClass, Boolean isList) {
        String content = this.getByKey(key);
        if (itemClass == String.class) {
            return (R) content;
        }
        if (JsonUtils.isEmpty(content)) {
            return null;
        }
        if (isList == null) {
            isList = JsonUtils.isArray(content);
        }
        if (isList) {
            return (R) JsonUtils.parseArray(content, itemClass);
        } else {
            return (R) JsonUtils.parseT(content, itemClass);
        }
    }

    /**
     * 获取dev、test、pre、prod对应环境的mongo缓存数据
     *
     * @param key       缓存key
     * @param itemClass 序列化数据的class
     * @param <R>       返回的数据类型
     * @param <Item>    返回数据的单个实体类型，若为对象Item与R类型一致
     * @return R类型的数据
     */
    public <R, Item> R get(String key, Class<Item> itemClass) {
        return get(key, itemClass, null);
    }

    public void cleanContainsPrefixCache(DbMethodEntry... methodEntry) {
        List<String> keys = Arrays.stream(methodEntry).map(MethodEntry::getCacheSubKey).collect(Collectors.toList());
        this.blurDelete(keys);
    }

    public void cleanContainsPrefixCache(Collection<DbMethodEntry> entries) {
        Set<String> keys = entries.stream().map(MethodEntry::getCacheSubKey).collect(Collectors.toSet());
        this.blurDelete(keys);
    }

    public void remove(String key) {
        this.deleteByKey(key);
    }
}
