package com.eyinfo.springcache.strategy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.storage.DbMethodEntry;
import com.eyinfo.springcache.storage.KeysStorage;
import com.eyinfo.springcache.storage.StorageUtils;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/9/13
 * @Description:
 * @Modifier:
 * @ModifyContent:
 */
public class DeleteStrategy extends BaseQueryStrategy {

    public void delete(DbMethodEntry methodEntry, String where) {
        String key = getQueryKey(methodEntry.getCacheSubKey(), where);
        if (TextUtils.isEmpty(key)) {
            return;
        }
        String objectUnique = KeysStorage.geObjectUnique(key, methodEntry, true);
        if (TextUtils.isEmpty(objectUnique)) {
            return;
        }
        StorageUtils.deleteCache(objectUnique);
    }

    public <T> void deletePlus(DbMethodEntry methodEntry, QueryWrapper<T> queryWrapper) {
        String key = getQueryKeyPlus(methodEntry.getCacheSubKey(), queryWrapper);
        if (TextUtils.isEmpty(key)) {
            return;
        }
        String objectUnique = KeysStorage.geObjectUnique(key, methodEntry, true);
        if (TextUtils.isEmpty(objectUnique)) {
            return;
        }
        StorageUtils.deleteCache(objectUnique);
    }
}
