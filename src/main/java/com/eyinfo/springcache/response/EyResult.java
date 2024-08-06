package com.eyinfo.springcache.response;

import com.eyinfo.foundation.entity.PageListResponse;
import com.eyinfo.foundation.entity.Result;
import com.github.pagehelper.PageInfo;

import java.util.List;

public class EyResult extends Result {
    public static <T> PageListResponse<List<T>> response(PageInfo<T> pageInfo) {
        PageListResponse<List<T>> response = new PageListResponse<>();
        response.setCount(pageInfo.getTotal());
        response.setData(pageInfo.getList());
        response.setIsFirstPage(pageInfo.isIsFirstPage());
        response.setHasNextPage(pageInfo.isHasNextPage());
        return response;
    }
}
