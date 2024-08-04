package com.eyinfo.springcache.entity;

import java.io.Serializable;

public class MongoCacheEntity implements Serializable {

    //通过缓存内容md5得到
    private String id;

    //缓存标识
    private String key;

    //缓存内容
    private String content;

    //缓存时间
    private long cacheTime;

    //缓存时间段
    private long period;

    //是否持久化缓存
    private boolean isPersistence;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public boolean isPersistence() {
        return isPersistence;
    }

    public void setPersistence(boolean persistence) {
        isPersistence = persistence;
    }
}
