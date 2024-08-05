package com.eyinfo.springcache.storage.entity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.HashMap;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020/7/3
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class PageConditions {
    //分页索引
    private int pageNumber;

    //每页记录数
    private int pageSize = 30;

    //条件
    private HashMap<String, Object> conditions;

    //前置条件
    private String precondition;

    //排序
    private String orderBy;

    //sql参数是否map类型
    private boolean isMap;

    //mybatis-plus查询条件
    private QueryWrapper queryWrapper;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

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

    public boolean isMap() {
        return isMap;
    }

    public void setMap(boolean map) {
        isMap = map;
    }

    public QueryWrapper getQueryWrapper() {
        return queryWrapper;
    }

    public void setQueryWrapper(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
    }
}
