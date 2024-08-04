package com.eyinfo.springcache.storage;

import com.eyinfo.foundation.utils.TextUtils;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019/1/14
 * Description:内存缓存
 * Modifier:
 * ModifyContent:
 */
public class MemoryCache {

    private static volatile MemoryCache memoryCache = null;

    private HashMap<String, Object> softMap = new HashMap<>();
    private SoftReference<HashMap<String, Object>> softReference = new SoftReference<>(softMap);

    public static MemoryCache getInstance() {
        if (memoryCache == null) {
            synchronized (MemoryCache.class) {
                if (memoryCache == null) {
                    memoryCache = new MemoryCache();
                }
            }
        }
        return memoryCache;
    }

    /**
     * 清除所有缓存
     */
    public void removeAll() {
        softMap.clear();
    }

    /**
     * 设置软缓存,在内存极度底下时被清理
     *
     * @param key     缓存键
     * @param content 缓存内容
     */
    public void set(String key, Object content) {
        if (TextUtils.isEmpty(key) || content == null) {
            return;
        }
        HashMap<String, Object> map = softReference.get();
        if (map == null) {
            softReference = new SoftReference<>(softMap);
            map = softReference.get();
            //再次检测
            if (map == null) {
                return;
            }
        }
        map.put(key, content);
    }

    /**
     * 获取软缓存,在内存极度底下时被清理
     *
     * @param key 缓存键
     */
    public <T> T get(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        HashMap<String, Object> map = softReference.get();
        if (map == null || !map.containsKey(key)) {
            return null;
        }
        Object o = map.get(key);
        if (o == null) {
            return null;
        } else {
            return (T) o;
        }
    }
}
