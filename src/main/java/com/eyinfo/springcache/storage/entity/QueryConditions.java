package com.eyinfo.springcache.storage.entity;

import java.util.HashMap;

public class QueryConditions {
    //条件
    private HashMap<String, Object> conditions;

    //前置条件
    private String precondition;

    //排序
    private String orderBy;

    public HashMap<String, Object> getConditions() {
        return conditions == null ? conditions = new HashMap<>() : conditions;
    }

    public void setConditions(HashMap<String, Object> conditions) {
        this.conditions = conditions;
    }

    public String getPrecondition() {
        return precondition == null ? "" : precondition;
    }

    public void setPrecondition(String precondition) {
        this.precondition = precondition;
    }

    public String getOrderBy() {
        return orderBy == null ? "" : orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
