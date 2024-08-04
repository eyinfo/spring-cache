package com.eyinfo.springcache.storage.strategy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.storage.DbMethodEntry;
import com.eyinfo.springcache.storage.KeysStorage;
import com.eyinfo.springcache.storage.StorageUtils;

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
        StorageUtils.save(data, objectUnique);
    }

    public <T> void savePlus(DbMethodEntry methodEntry, QueryWrapper<T> queryWrapper, Object data, boolean isMergeSave) {
        if (data == null) {
            return;
        }
        String key = getQueryKeyPlus(methodEntry.getCacheSubKey(), queryWrapper);
        if (TextUtils.isEmpty(key)) {
            return;
        }
        String objectUnique = isMergeSave ? KeysStorage.geObjectUnique(key, methodEntry, false) : key;
        StorageUtils.save(data, objectUnique);
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
            return StorageUtils.get(objectUnique, itemClass, isList);
        } else {
            return StorageUtils.get(key, itemClass, isList);
        }
    }

    public <R, Item> R queryPlus(DbMethodEntry methodEntry, QueryWrapper queryWrapper, boolean isMergeQuery, Class<Item> itemClass, boolean isList) {
        String key = getQueryKeyPlus(methodEntry.getCacheSubKey(), queryWrapper);
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        if (isMergeQuery) {
            String objectUnique = KeysStorage.geObjectUnique(key, methodEntry, true);
            return StorageUtils.get(objectUnique, itemClass, isList);
        } else {
            return StorageUtils.get(key, itemClass, isList);
        }
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
