package com.eyinfo.springcache.mongo;

import com.eyinfo.foundation.enums.Environment;

public class LogicDeleteDataManager extends BaseMongo {
    private static LogicDeleteDataManager logicDeleteDataManager;

    public static LogicDeleteDataManager getInstance() {
        if (logicDeleteDataManager == null) {
            synchronized (LogicDeleteDataManager.class) {
                if (logicDeleteDataManager == null) {
                    logicDeleteDataManager = new LogicDeleteDataManager();
                }
            }
        }
        return logicDeleteDataManager;
    }

    @Override
    protected String getCollectionName(Environment environment) {
        return String.format("tbl_logic_delete_%s", environment.name());
    }
}
