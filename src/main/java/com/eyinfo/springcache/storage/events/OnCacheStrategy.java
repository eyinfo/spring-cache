package com.eyinfo.springcache.storage.events;

import com.eyinfo.springcache.storage.DbMethodEntry;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020/7/4
 * Description:接口查询时对数据缓存做两方便处理
 * 根据MethodEntry.cacheSubKey做业务缓存的存取
 * Modifier:
 * ModifyContent:
 */
public class OnCacheStrategy<C, R, Item> {

    /**
     * 根据subkey取出缓存数据
     *
     * @param methodEntry
     * @param conditions  条件
     * @return 返回缓存数据
     */
    public R onQueryCache(DbMethodEntry methodEntry, C conditions, Class<Item> targetClass) {
        return null;
    }

    /**
     * 根据subkey对从db查询的数据做缓存处理
     *
     * @param methodEntry
     * @param conditions  条件
     * @param data        要缓存的数据
     * @param targetClass 目标数据类型
     */
    public void onDataCache(DbMethodEntry methodEntry, C conditions, Object data, Class<Item> targetClass) {

    }

    /**
     * 清除缓存
     *
     * @param methodEntry DbMethodEntry
     * @param conditions  条件
     */
    public void onRemoveCache(DbMethodEntry methodEntry, C conditions, Class<Item> targetClass) {
    }
}
