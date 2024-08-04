package com.eyinfo.springcache.storage.entity;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/9/22
 * @Description:
 * @Modifier:
 * @ModifyContent:
 */
public class ObjectEntry implements Serializable {

    private HashMap<String, String> objectKeys;
    private String unique;

    public HashMap<String, String> getObjectKeys() {
        return objectKeys == null ? objectKeys = new HashMap<>() : objectKeys;
    }

    public void setObjectKeys(HashMap<String, String> objectKeys) {
        this.objectKeys = objectKeys;
    }

    public String getUnique() {
        return unique == null ? "" : unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }
}
