package com.eyinfo.springcache.storage.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.entity.BaseEntity;
import com.eyinfo.foundation.entity.PageListResponse;
import com.eyinfo.springcache.response.EyResult;
import com.eyinfo.springcache.storage.DbMethodEntry;
import com.eyinfo.springcache.storage.StorageManager;
import com.eyinfo.springcache.storage.entity.PageConditions;
import com.eyinfo.springcache.storage.entity.PageRequest;
import com.github.pagehelper.PageInfo;

import java.util.List;

public class BaseService<T extends BaseEntity, M extends ItemMapper<T>> {

    /**
     * 查询单条数据
     *
     * @param mapper       mapper
     * @param queryWrapper 查询条件
     * @return 数据对象
     */
    public T findOne(M mapper, QueryWrapper<T> queryWrapper) {
        String sqlSegment = queryWrapper.getSqlSegment();
        if (!sqlSegment.contains("limit")) {
            queryWrapper.last("limit 1");
        }
        return mapper.getDataPlus(queryWrapper);
    }

    /**
     * 插入或更新数据
     *
     * @param mapper mapper
     * @param entity 数据实体
     * @return 数据id
     */
    public int insertOrUpdate(M mapper, T entity) {
        if (entity.getId() == null || entity.getId() == 0) {
            return mapper.insertSelective(entity);
        } else {
            return mapper.updateByPrimaryKeySelective(entity);
        }
    }

    /**
     * 查询分页数据
     *
     * @param mapper       mapper
     * @param request      分页参数
     * @param queryWrapper 查询条件
     * @param itemClass    单条数据class类型
     * @param methodEntry  查询缓存定义
     * @param skipCache    是否跳过缓存
     * @return
     */
    public PageListResponse<List<T>> getPageList(M mapper, PageRequest request, QueryWrapper queryWrapper, Class<T> itemClass, DbMethodEntry methodEntry, boolean skipCache) {
        PageConditions conditions = new PageConditions();
        conditions.setQueryWrapper(queryWrapper == null ? new QueryWrapper() : queryWrapper);
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getLimit() == null) {
            request.setLimit(10);
        }
        if (request.getPage() > 0) {
            conditions.setPageNumber(request.getPage());
        }
        if (request.getLimit() > 0) {
            conditions.setPageSize(request.getLimit());
        }
        PageInfo<T> pageInfo = StorageManager.getInstance().queryPage(mapper, itemClass, methodEntry, conditions, skipCache);
        return EyResult.response(pageInfo);
    }

    /**
     * 查询分页数据
     *
     * @param mapper       mapper
     * @param request      分页参数
     * @param queryWrapper 查询条件
     * @param itemClass    单条数据class类型
     * @param methodEntry  查询缓存定义
     * @return
     */
    public PageListResponse<List<T>> getPageList(M mapper, PageRequest request, QueryWrapper queryWrapper, Class<T> itemClass, DbMethodEntry methodEntry) {
        return this.getPageList(mapper, request, queryWrapper, itemClass, methodEntry, false);
    }

    /**
     * 查询分页数据
     *
     * @param mapper      mapper
     * @param request     分页参数
     * @param itemClass   单条数据class类型
     * @param methodEntry 查询缓存定义
     * @return
     */
    public PageListResponse<List<T>> getPageList(M mapper, PageRequest request, Class<T> itemClass, DbMethodEntry methodEntry) {
        return this.getPageList(mapper, request, null, itemClass, methodEntry, false);
    }

    /**
     * 查询分页数据
     *
     * @param mapper       mapper
     * @param page         查询分页
     * @param limit        每页显示大小
     * @param queryWrapper 查询条件
     * @param itemClass    单条数据class类型
     * @param methodEntry  查询缓存定义
     * @param skipCache    是否跳过缓存
     * @return
     */
    public PageListResponse<List<T>> getPageList(M mapper, int page, int limit, QueryWrapper queryWrapper, Class<T> itemClass, DbMethodEntry methodEntry, boolean skipCache) {
        PageRequest request = new PageRequest();
        request.setPage(page);
        request.setLimit(limit);
        return this.getPageList(mapper, request, queryWrapper, itemClass, methodEntry, skipCache);
    }

    /**
     * 查询分页数据
     *
     * @param mapper       mapper
     * @param page         查询分页
     * @param limit        每页显示大小
     * @param itemClass    单条数据class类型
     * @param methodEntry  查询缓存定义
     * @param queryWrapper 查询条件
     * @return
     */
    public PageListResponse<List<T>> getPageList(M mapper, int page, int limit, Class<T> itemClass, DbMethodEntry methodEntry, QueryWrapper queryWrapper) {
        return this.getPageList(mapper, page, limit, queryWrapper, itemClass, methodEntry, false);
    }

    /**
     * 查询分页数据
     *
     * @param mapper      mapper
     * @param page        查询分页
     * @param limit       每页显示大小
     * @param itemClass   单条数据class类型
     * @param methodEntry 查询缓存定义
     * @return
     */
    public PageListResponse<List<T>> getPageList(M mapper, int page, int limit, Class<T> itemClass, DbMethodEntry methodEntry) {
        return this.getPageList(mapper, page, limit, null, itemClass, methodEntry, false);
    }

    public boolean isExist(M mapper, QueryWrapper queryWrapper) {
        return mapper.countPlus(queryWrapper) > 0;
    }

    public boolean isExist(T entity) {
        return entity != null && entity.getId() > 0;
    }
}
