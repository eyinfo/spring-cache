package com.eyinfo.springcache.storage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.storage.events.OnDeleteStrategy;
import com.eyinfo.springcache.storage.invoke.InvokeResult;

class DeleteService extends BaseService {

    public <Dao> void delete(Dao dao, DbMethodEntry methodEntry, String where, OnDeleteStrategy<String> deleteStrategy) {
        if (TextUtils.isEmpty(methodEntry.getMethodName())) {
            methodEntry.setMethodName("delete");
        }
        if (!deleteFromDB(dao, methodEntry, where)) {
            return;
        }
        if (deleteStrategy == null) {
            return;
        }
        deleteStrategy.onDeleteCache(methodEntry, where);
    }

    private <Dao> boolean deleteFromDB(Dao dao, DbMethodEntry methodEntry, String where) {
        InvokeResult invokeResult = super.invokeWithString(dao, methodEntry, where);
        return invokeResult.isSuccess();
    }

    public <Dao, T> void deletePlus(Dao dao, DbMethodEntry methodEntry, QueryWrapper<T> queryWrapper, OnDeleteStrategy<QueryWrapper<T>> deleteStrategy) {
        if (TextUtils.isEmpty(methodEntry.getMethodName())) {
            methodEntry.setMethodName("delete");
        }
        if (!deletePlusFromDB(dao, methodEntry, queryWrapper)) {
            return;
        }
        if (deleteStrategy == null) {
            return;
        }
        deleteStrategy.onDeleteCache(methodEntry, queryWrapper);
    }

    private <Dao, T> boolean deletePlusFromDB(Dao dao, DbMethodEntry methodEntry, QueryWrapper<T> queryWrapper) {
        InvokeResult invokeResult = super.invoke(dao, methodEntry, queryWrapper);
        return invokeResult.isSuccess();
    }
}
