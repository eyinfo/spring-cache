package com.eyinfo.springcache.storage.events;

import com.eyinfo.springcache.storage.DbMethodEntry;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/9/13
 * @Description:
 * @Modifier:
 * @ModifyContent:
 */
public interface OnDeleteStrategy<C, T> {

    /**
     * 根据subkey取出缓存数据
     *
     * @param methodEntry
     * @param conditions   条件
     * @param redisDbIndex redis缓存数据库索引
     * @return 返回缓存数据
     */
    public void onDeleteCache(DbMethodEntry methodEntry, C conditions, Class<T> targetClass);
}
