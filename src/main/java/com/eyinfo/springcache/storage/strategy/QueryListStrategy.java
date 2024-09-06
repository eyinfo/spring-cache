package com.eyinfo.springcache.storage.strategy;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.utils.ObjectJudge;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.entity.CachingStrategyConfig;
import com.eyinfo.springcache.mongo.MongoManager;
import com.eyinfo.springcache.storage.DbMethodEntry;
import com.eyinfo.springcache.storage.StorageUtils;
import com.eyinfo.springcache.storage.entity.SearchCondition;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/9/11
 * @Description:查询列表策略
 * @Modifier:
 * @ModifyContent:
 */
public class QueryListStrategy extends BaseQueryStrategy {

    private void saveData(DbMethodEntry methodEntry, String key, Object data) {
        if (!(data instanceof PageInfo) || TextUtils.isEmpty(key)) {
            return;
        }
        PageInfo pageInfo = (PageInfo) data;
        List list = pageInfo.getList();
        if (ObjectJudge.isNullOrEmpty(list)) {
            return;
        }
        Long cacheTimestamp = methodEntry.getCacheTimestamp();
        if (cacheTimestamp == null) {
            CachingStrategyConfig strategyConfig = StorageUtils.getCachingStrategyConfig();
            MongoManager.getInstance().save(key, pageInfo, strategyConfig.getApiGlobalCacheTime());
        } else {
            MongoManager.getInstance().save(key, pageInfo, cacheTimestamp);
        }
    }

    public <Item> void save(DbMethodEntry methodEntry, SearchCondition conditions, Object data, Class<Item> itemClass) {
        String key = getCacheKey(methodEntry, conditions, itemClass);
        saveData(methodEntry, key, data);
    }

    private <Item> String getCacheKey(DbMethodEntry methodEntry, SearchCondition conditions, Class<Item> itemClass) {
        String prefix;
        if (itemClass == null) {
            prefix = methodEntry.getCacheSubKey();
        } else {
            TableName declaredAnnotation = itemClass.getDeclaredAnnotation(TableName.class);
            if (declaredAnnotation == null) {
                prefix = methodEntry.getCacheSubKey();
            } else {
                String value = declaredAnnotation.value();
                prefix = TextUtils.isEmpty(value) ? methodEntry.getCacheSubKey() : String.format("%s_%s", value, methodEntry.getCacheSubKey());
            }
        }
        return getQueryKey(prefix, conditions);
    }

    private <Item> String getCacheKey(DbMethodEntry methodEntry, QueryWrapper queryWrapper, Class<Item> itemClass) {
        String prefix;
        if (itemClass == null) {
            prefix = methodEntry.getCacheSubKey();
        } else {
            TableName declaredAnnotation = itemClass.getDeclaredAnnotation(TableName.class);
            if (declaredAnnotation == null) {
                prefix = methodEntry.getCacheSubKey();
            } else {
                String value = declaredAnnotation.value();
                prefix = TextUtils.isEmpty(value) ? methodEntry.getCacheSubKey() : String.format("%s_%s", value, methodEntry.getCacheSubKey());
            }
        }
        return getQueryKeyPlus(prefix, queryWrapper);
    }

    public <Item> void savePlus(DbMethodEntry methodEntry, QueryWrapper queryWrapper, Object data, Class<Item> itemClass) {
        String key = getCacheKey(methodEntry, queryWrapper, itemClass);
        saveData(methodEntry, key, data);
    }

    public void save(DbMethodEntry methodEntry, String conditions, Object data) {
        String key = getQueryKey(methodEntry.getCacheSubKey(), conditions);
        saveData(methodEntry, key, data);
    }

    private <R, Item> R queryData(DbMethodEntry methodEntry, String key, Class<Item> itemClass, boolean isList) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        return MongoManager.getInstance().get(key, itemClass, isList);
    }

    public <R, Item> R query(DbMethodEntry methodEntry, SearchCondition conditions, Class<Item> itemClass, boolean isList) {
        String key = getCacheKey(methodEntry, conditions, itemClass);
        return queryData(methodEntry, key, itemClass, isList);
    }

    public <R, Item> R queryPlus(DbMethodEntry methodEntry, QueryWrapper queryWrapper, Class<Item> itemClass, boolean isList) {
        String key = getCacheKey(methodEntry, queryWrapper, itemClass);
        return queryData(methodEntry, key, itemClass, isList);
    }

    public <R, Item> R query(DbMethodEntry methodEntry, String conditions, Class<Item> itemClass, boolean isList) {
        String key = getQueryKey(methodEntry.getCacheSubKey(), conditions);
        return queryData(methodEntry, key, itemClass, isList);
    }

    //清除包含前缀缓存
    public void cleanContainsPrefixCache(DbMethodEntry... methodEntry) {
        MongoManager.getInstance().cleanContainsPrefixCache(methodEntry);
    }
}
