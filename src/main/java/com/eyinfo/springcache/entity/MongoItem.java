package com.eyinfo.springcache.entity;

import java.io.Serializable;

public class MongoItem implements Serializable {

    /**
     * 存储数据key
     */
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
