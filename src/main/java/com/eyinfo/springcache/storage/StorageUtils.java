package com.eyinfo.springcache.storage;

import com.eyinfo.foundation.enums.Environment;
import com.eyinfo.springcache.entity.CachingStrategyConfig;

public class StorageUtils {

    static StorageConfiguration configuration;

    public static StorageConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * 获取当前运行时环境
     *
     * @{StorageConfiguration}未配置时默认为dev-开发环境
     */
    public static Environment getEnvironment() {
        if (configuration == null) {
            return Environment.dev;
        }
        String active = configuration.getActive();
        Environment environment = Environment.getEnvironment(active);
        return environment == null ? Environment.dev : environment;
    }

    public static CachingStrategyConfig getCachingStrategyConfig() {
        CachingStrategyConfig strategyConfig = configuration.getCachingStrategyConfig();
        return strategyConfig == null ? new CachingStrategyConfig() : strategyConfig;
    }
}
