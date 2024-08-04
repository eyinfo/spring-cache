package com.eyinfo.springcache.storage.entity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.HashMap;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/9/30
 * @Description:查询条件
 * @Modifier:
 * @ModifyContent:
 */
public class SearchCondition {

    //分页索引
    private int pageNumber;

    //每页记录数
    private int pageSize = 30;

    //由PageCondition生成的条件sql
    private String conditionSql;

    //map参数
    private HashMap<String, Object> params;

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

    public String getConditionSql() {
        return conditionSql == null ? "" : conditionSql;
    }

    public void setConditionSql(String conditionSql) {
        this.conditionSql = conditionSql;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

    public QueryWrapper getQueryWrapper() {
        return queryWrapper;
    }

    public void setQueryWrapper(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
    }
}
