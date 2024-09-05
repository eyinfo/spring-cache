package com.eyinfo.springcache.storage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.storage.enums.Methods;
import com.eyinfo.springcache.storage.events.OnDeleteStrategy;
import com.eyinfo.springcache.storage.invoke.InvokeResult;
import com.eyinfo.springcache.storage.mybatis.ItemMapper;
import com.eyinfo.springcache.storage.mybatis.PrototypeMapper;

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

    public <Dao extends PrototypeMapper<?>> void deletePlus(Dao dao, DbMethodEntry methodEntry, QueryWrapper queryWrapper, OnDeleteStrategy<QueryWrapper> deleteStrategy) {
        if (!deletePlusFromDB(dao, methodEntry, queryWrapper)) {
            return;
        }
        if (deleteStrategy != null) {
            deleteStrategy.onDeleteCache(methodEntry, queryWrapper);
        }
    }

    private <Dao extends PrototypeMapper<?>> boolean deletePlusFromDB(Dao dao, DbMethodEntry methodEntry, QueryWrapper queryWrapper) {
        if (dao instanceof ItemMapper<?> && !TextUtils.equals(methodEntry.getMethodName(), Methods.deletePlus.name())) {
            ((ItemMapper<?>) dao).deletePlus(queryWrapper);
            return true;
        } else {
            InvokeResult invokeResult = super.invoke(dao, methodEntry, queryWrapper);
            return invokeResult.isSuccess();
        }
    }
}
