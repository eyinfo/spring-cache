package com.eyinfo.springcache.storage.entity;

import java.util.HashMap;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019/9/22
 * Description:分页请求
 * Modifier:
 * ModifyContent:
 */
public class PageRequest {
    /**
     * 分页索引
     */
    private Integer page;
    /**
     * 分页大小
     */
    private Integer limit;
    /**
     * 查询关键字
     */
    private String keyword;
    /**
     * 查询条件
     */
    private HashMap<String, String> conditions;

    public Integer getPage() {
        return page == null ? 1 : page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit == null ? 10 : limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public HashMap<String, String> getConditions() {
        if (conditions == null) {
            conditions = new HashMap<String, String>();
        }
        return conditions;
    }

    public void setConditions(HashMap<String, String> conditions) {
        this.conditions = conditions;
    }
}
