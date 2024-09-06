package com.eyinfo.springcache.storage;

import com.eyinfo.springcache.enums.DataType;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020/7/2
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class DbMethodEntry extends MethodEntry {

    //数据类型
    private final DataType dataType;

    private final Long cacheTimestamp;

    public DbMethodEntry(String methodName, String cacheSubKey, Long cacheTimestamp, DataType dataType, Class<?>... parameterTypes) {
        super(methodName, "9c74ae9a1b498765", cacheSubKey, parameterTypes);
        this.dataType = dataType;
        this.cacheTimestamp = cacheTimestamp;
    }

    public DbMethodEntry(String methodName, String cacheSubKey, DataType dataType, Class<?>... parameterTypes) {
        this(methodName, cacheSubKey, null, dataType, parameterTypes);
    }

    public String getDataType() {
        if (this.dataType == null) {
            return "";
        }
        return this.dataType.name();
    }

    public Long getCacheTimestamp() {
        if (this.cacheTimestamp == null || this.cacheTimestamp <= 0) {
            return null;
        }
        return cacheTimestamp;
    }
}
