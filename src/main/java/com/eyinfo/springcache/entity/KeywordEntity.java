package com.eyinfo.springcache.entity;

public class KeywordEntity {
    private String id;
    //关键字
    private String keyword;
    //分页大小
    private int pageSize;
    //页码
    private int pageNumber;
    //记录总数
    private int total;
    //搜索统计
    private int count;
    //是否完成通过三方api查询
    private boolean isOpenFinish;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isOpenFinish() {
        return isOpenFinish;
    }

    public void setOpenFinish(boolean openFinish) {
        isOpenFinish = openFinish;
    }
}
