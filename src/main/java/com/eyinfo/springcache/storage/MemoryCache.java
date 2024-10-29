package com.eyinfo.springcache.storage;

import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.foundation.utils.TimeSyncUtils;
import org.springframework.data.redis.core.TimeoutUtils;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019/1/14
 * Description:内存缓存
 * Modifier:
 * ModifyContent:
 */
class MemoryCacheEntry {
    private Object content;
    private long period;
    private TimeUnit unit;
    private long startTime;

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}

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
     * 保存数据
     *
     * @param key     缓存key
     * @param content 缓存内容
     * @param period  缓存时间
     * @param unit    缓存时间单位
     */
    public void set(String key, Object content, long period, TimeUnit unit) {
        MemoryCacheEntry cacheEntry = new MemoryCacheEntry();
        cacheEntry.setContent(content);
        cacheEntry.setPeriod(period);
        cacheEntry.setUnit(unit);
        cacheEntry.setStartTime(TimeSyncUtils.getCurrentTimestamp());
        this.set(key, cacheEntry);
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
        }
        if (o instanceof MemoryCacheEntry) {
            MemoryCacheEntry entry = (MemoryCacheEntry) o;
            long millis = TimeoutUtils.toMillis(entry.getPeriod(), entry.getUnit());
            long startTime = entry.getStartTime();
            long endTime = startTime + millis;
            boolean expired = TimeSyncUtils.getCurrentTimestamp() > endTime;
            if (expired) {
                map.remove(key);
                return null;
            }
            return (T) entry.getContent();
        }
        return (T) o;
    }

    public void remove(String key) {
        softMap.remove(key);
    }
}
