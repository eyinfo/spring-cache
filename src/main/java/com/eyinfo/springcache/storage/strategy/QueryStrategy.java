package com.eyinfo.springcache.storage.strategy;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.mongo.MongoManager;
import com.eyinfo.springcache.storage.DbMethodEntry;
import com.eyinfo.springcache.storage.KeysStorage;
import com.eyinfo.springcache.storage.entity.SearchCondition;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/9/12
 * @Description:
 * @Modifier:
 * @ModifyContent:
 */
public class QueryStrategy extends BaseQueryStrategy {

    /**
     * 缓存数据s
     *
     * @param methodEntry 对应数据方法配置
     * @param where       查询条件
     * @param data        缓存数据对象
     * @param isMergeSave 与缓存查询的isMergeQuery保持一致；
     *                    true-将当前查询条件转换成统一的key进行缓存
     *                    false-直接根据当前查询条件的md5值缓存
     */
    public void save(DbMethodEntry methodEntry, String where, Object data, boolean isMergeSave) {
        if (data == null) {
            return;
        }
        String key = getQueryKey(methodEntry.getCacheSubKey(), where);
        if (TextUtils.isEmpty(key)) {
            return;
        }
        String objectUnique = isMergeSave ? KeysStorage.geObjectUnique(key, methodEntry, false) : key;
        super.cacheData(objectUnique, data);
    }

    public <T> void savePlus(DbMethodEntry methodEntry, QueryWrapper<T> queryWrapper, Object data, boolean isMergeSave,Class<T> targetClass) {
        if (data == null) {
            return;
        }
        String key = getCacheKey(methodEntry, queryWrapper,targetClass);
        super.cacheData(key, data);
    }

    /**
     * 查询 数据实体
     *
     * @param methodEntry  对应数据方法配置
     * @param where        查询条件
     * @param isMergeQuery true-根据当前查询条件获取某一对象的唯一值，再根据该唯一值返回数据；（即:对同一对象可以根据不同查询条件返回）
     *                     false-仅根据查询条件返回；(若返回null最后会根据DB查询返回)
     * @param itemClass    Item class
     * @param isList       是否列表查询
     * @return 数据实体
     */
    public <R, Item> R query(DbMethodEntry methodEntry, String where, boolean isMergeQuery, Class<Item> itemClass, boolean isList) {
        String key = getQueryKey(methodEntry.getCacheSubKey(), where);
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        if (isMergeQuery) {
            String objectUnique = KeysStorage.geObjectUnique(key, methodEntry, true);
            return MongoManager.getInstance().get(objectUnique, itemClass, isList);
        } else {
            return MongoManager.getInstance().get(key, itemClass, isList);
        }
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

    public <R, Item> R queryPlus(DbMethodEntry methodEntry, QueryWrapper queryWrapper, boolean isMergeQuery, Class<Item> itemClass, boolean isList) {
        String key = getCacheKey(methodEntry, queryWrapper,itemClass);
        return MongoManager.getInstance().get(key, itemClass, isList);
    }

    /**
     * 查询 数据实体
     *
     * @param methodEntry 对应数据方法配置
     * @param where       查询条件
     * @return 数据实体
     */
    public <R, Item> R query(DbMethodEntry methodEntry, String where, Class<Item> itemClass, boolean isList) {
        return query(methodEntry, where, true, itemClass, isList);
    }

    public <R, Item> R queryPlus(DbMethodEntry methodEntry, QueryWrapper<Item> queryWrapper, Class<Item> itemClass, boolean isList) {
        return queryPlus(methodEntry, queryWrapper, true, itemClass, isList);
    }
}
