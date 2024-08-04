package com.eyinfo.springcache.storage.entity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.springcache.storage.events.ModelConditions;

public class ModelCacheConditions extends ModelConditions {

    //查询条件
    private String where;

    //是否合并查询
    private boolean isMergeQuery;

    private QueryWrapper queryWrapper;

    @Override
    public String getWhere() {
        return where == null ? "" : where;
    }

    @Override
    public QueryWrapper getQueryWrapper() {
        return queryWrapper;
    }

    public void setQueryWrapper(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    @Override
    public boolean isMergeQuery() {
        return isMergeQuery;
    }

    public void setMergeQuery(boolean mergeQuery) {
        isMergeQuery = mergeQuery;
    }
}
