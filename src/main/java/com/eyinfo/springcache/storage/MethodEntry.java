package com.eyinfo.springcache.storage;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020/7/1
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class MethodEntry {

    //方法名
    private String methodName;

    //参数类型
    private Class<?>[] parameterTypes;

    //缓存数据库名
    private String dbName;

    //缓存key
    private String cacheSubKey;

    public MethodEntry(String methodName, String dbName, String cacheSubKey, Class<?>... parameterTypes) {
        this.methodName = methodName;
        this.dbName = dbName;
        this.cacheSubKey = cacheSubKey;
        this.parameterTypes = parameterTypes;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }

    public void setParameterTypes(Class<?>... parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getDbName() {
        return dbName == null ? "" : dbName;
    }

    public String getCacheSubKey() {
        return cacheSubKey;
    }
}
