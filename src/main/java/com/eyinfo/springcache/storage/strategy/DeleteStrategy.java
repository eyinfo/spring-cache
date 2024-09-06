package com.eyinfo.springcache.storage.strategy;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.mongo.MongoManager;
import com.eyinfo.springcache.storage.DbMethodEntry;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/9/13
 * @Description:
 * @Modifier:
 * @ModifyContent:
 */
public class DeleteStrategy extends BaseQueryStrategy {

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
                prefix = TextUtils.isEmpty(value) ? methodEntry.getCacheSubKey() : value;
            }
        }
        return getQueryKeyPlus(prefix, queryWrapper);
    }

    public <T> void deletePlus(DbMethodEntry methodEntry, QueryWrapper<T> queryWrapper, Class<T> targetClass) {
        String key = getCacheKey(methodEntry, queryWrapper, targetClass);
        MongoManager.getInstance().remove(key);
    }
}
