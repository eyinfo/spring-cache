package com.eyinfo.springcache.storage.events;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public abstract class ModelConditions {
    public abstract String getWhere();

    public abstract QueryWrapper getQueryWrapper();

    public abstract boolean isMergeQuery();
}
