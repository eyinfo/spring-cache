package com.eyinfo.springcache.mongo;

import com.eyinfo.foundation.enums.Environment;

public class MongoManager extends BaseMongo {

    private static MongoManager mongoManager;

    public static MongoManager getInstance() {
        if (mongoManager == null) {
            synchronized (MongoManager.class) {
                if (mongoManager == null) {
                    mongoManager = new MongoManager();
                }
            }
        }
        return mongoManager;
    }

    @Override
    protected String getCollectionName(Environment environment) {
        return String.format("tbl_cache_record_%s", environment.name());
    }
}
