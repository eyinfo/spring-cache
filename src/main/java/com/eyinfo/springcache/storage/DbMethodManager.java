package com.eyinfo.springcache.storage;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.eyinfo.springcache.enums.DataType;

public class DbMethodManager {

    public static DbMethodEntry getList(String cacheSubKey) {
        return new DbMethodEntry("getListPlus", cacheSubKey, DataType.list, Wrapper.class);
    }

    public static DbMethodEntry delete(String cacheSubKey) {
        return new DbMethodEntry("deletePlus", cacheSubKey, DataType.dynamic, Wrapper.class);
    }

    public static DbMethodEntry find(String cacheSubKey) {
        return new DbMethodEntry("getDataPlus", cacheSubKey, DataType.dynamic, Wrapper.class);
    }
}
