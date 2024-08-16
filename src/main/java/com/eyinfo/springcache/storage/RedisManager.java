package com.eyinfo.springcache.storage;

import com.eyinfo.foundation.events.OnRedisPropertyCall;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020/7/2
 * Description:https://yq.aliyun.com/articles/645266/
 * Modifier:
 * ModifyContent:
 */
public class RedisManager {

    private static RedisManager redisManager;

    private RedisManager() {

    }

    public static RedisManager getInstance() {
        if (redisManager == null) {
            synchronized (RedisManager.class) {
                if (redisManager == null) {
                    redisManager = new RedisManager();
                }
            }
        }
        return redisManager;
    }

}
