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

    //用于新增或更新时查询方法名
    private String queryMethodName;
    //数据类型
    private final DataType dataType;

    public DbMethodEntry(String methodName, String cacheSubKey, DataType dataType, Class<?>... parameterTypes) {
        super(methodName, "9c74ae9a1b498765", cacheSubKey, parameterTypes);
        this.dataType = dataType;
    }

    public DbMethodEntry cloneEntry(String methodName, DataType dataType) {
        return new DbMethodEntry(methodName, this.getCacheSubKey(), dataType, this.getParameterTypes());
    }

    public DbMethodEntry cloneEntry(DataType dataType) {
        return cloneEntry(this.getMethodName(), dataType);
    }

    public String getQueryMethodName() {
        return queryMethodName == null ? "find" : queryMethodName;
    }

    public DbMethodEntry setQueryMethodName(String queryMethodName) {
        this.queryMethodName = queryMethodName;
        return this;
    }

    public String getDataType() {
        if (this.dataType == null) {
            return "";
        }
        return this.dataType.name();
    }
}
