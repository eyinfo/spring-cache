package com.eyinfo.springcache.storage.strategy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.encrypts.MD5Encrypt;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.entity.CachingStrategyConfig;
import com.eyinfo.springcache.mongo.MongoManager;
import com.eyinfo.springcache.storage.KeysStorage;
import com.eyinfo.springcache.storage.StorageUtils;
import com.eyinfo.springcache.storage.entity.SearchCondition;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/9/12
 * @Description:
 * @Modifier:
 * @ModifyContent:
 */
public class BaseQueryStrategy {

    //md5(cacheSubKey+conditions)作为缓存key,缓存60秒
    public String getQueryKey(String cacheSubKey, SearchCondition conditions) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("pageNumber=%s&", conditions.getPageNumber()));
        builder.append(String.format("pageSize=%s&", conditions.getPageSize()));
        QueryWrapper queryWrapper = conditions.getQueryWrapper();
        if (queryWrapper == null) {
            builder.append(conditions.getConditionSql());
        } else {
            builder.append(KeysStorage.combQueryWrapper(queryWrapper));
        }
        return getQueryKey(cacheSubKey, builder.toString());
    }

    //md5(cacheSubKey+primary)作为缓存key,永久缓存
    public String getQueryKey(String cacheSubKey, String where) {
        if (TextUtils.isEmpty(cacheSubKey)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(cacheSubKey).append("&");
        builder.append(where);
        String key = MD5Encrypt.md5(builder.toString());
        return String.format("%s_%s", cacheSubKey, key);
    }

    public <T> String getQueryKeyPlus(String cacheSubKey, QueryWrapper<T> queryWrapper) {
        if (TextUtils.isEmpty(cacheSubKey)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(cacheSubKey).append("&");
        builder.append(KeysStorage.combQueryWrapper(queryWrapper));
        String key = MD5Encrypt.md5(builder.toString());
        return String.format("%s_%s", cacheSubKey, key);
    }

    protected void cacheData(String cacheKey, Object data) {
        CachingStrategyConfig strategyConfig = StorageUtils.getCachingStrategyConfig();
        Long apiGlobalCacheTime = strategyConfig.getApiGlobalCacheTime();
        if (apiGlobalCacheTime != null && apiGlobalCacheTime > 0) {
            MongoManager.getInstance().save(cacheKey, data, apiGlobalCacheTime);
        } else {
            MongoManager.getInstance().save(cacheKey, data, null);
        }
    }
}
