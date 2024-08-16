package com.eyinfo.springcache.storage;

import com.eyinfo.foundation.utils.GlobalUtils;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.redis.RedisManager;
import com.eyinfo.springcache.storage.entity.ObjectEntry;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/9/22
 * @Description:数据唯一标识与存储key映射(redis存储)
 * @Modifier:
 * @ModifyContent:
 */
public class KeysStorage {

    private static ObjectEntry addObjectEntry(String objectKey, DbMethodEntry methodEntry) {
        ObjectEntry entry = new ObjectEntry();
        HashMap<String, String> objectKeys = entry.getObjectKeys();
        objectKeys.put(objectKey, methodEntry.getDataType());
        entry.setUnique(GlobalUtils.getGuidNoConnect());
        RedisManager.getInstance().set(methodEntry.getCacheSubKey(), entry, 2, TimeUnit.DAYS);
        return entry;
    }

    //同一对象不同操作用此唯一标识
    //获取数据缓存对象key,对应关系:
    //| objectKey |          group         | unique |  data type  |
    //|:---------:|:----------------------:|:------:|:-----------:|
    //|    key1   | dbMethodEntry#cacheKey | uuid-1 |  dynamic    |
    //|    key2   | dbMethodEntry#cacheKey | uuid-1 |  list      |
    //|    key3   | dbMethodEntry#cacheKey |
    // uuid-1 |  dynamic   |
    //isQuery:指本次操作是否获取数据
    public static String geObjectUnique(String objectKey, DbMethodEntry methodEntry, boolean isQuery) {
        ObjectEntry entry = RedisManager.getInstance().get(methodEntry.getCacheSubKey());
        if (entry == null || TextUtils.isEmpty(entry.getUnique())) {
            entry = addObjectEntry(objectKey, methodEntry);
        }
        HashMap<String, String> objectKeys = entry.getObjectKeys();
        if (!objectKeys.containsKey(objectKey) && !isQuery) {
            String dataType = methodEntry.getDataType();
            objectKeys.put(objectKey, dataType);
            RedisManager.getInstance().set(methodEntry.getCacheSubKey(), entry, 2, TimeUnit.DAYS);
        }
        return objectKey;
    }
}
