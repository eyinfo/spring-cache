package com.eyinfo.springcache.storage.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.entity.BaseEntity;
import com.eyinfo.foundation.entity.PageListResponse;
import com.eyinfo.springcache.storage.DbMethodEntry;
import com.eyinfo.springcache.storage.StorageManager;
import com.eyinfo.springcache.storage.entity.PageRequest;

import java.util.List;

public class BaseService<T extends BaseEntity, M extends ItemMapper<T>> {

    /**
     * 查询单条数据
     *
     * @param mapper         mapper
     * @param queryWrapper   查询条件
     * @param cacheTimestamp 缓存时间戳
     * @param itemClass      单条数据class类型
     * @return 数据对象
     */
    public T findOne(M mapper, QueryWrapper<T> queryWrapper, Long cacheTimestamp, Class<T> itemClass) {
        return StorageManager.getInstance().findOne(mapper, queryWrapper, cacheTimestamp, itemClass);
    }

    /**
     * 查询单条数据
     *
     * @param mapper         mapper
     * @param id             数据id
     * @param cacheTimestamp 缓存时间戳
     * @param itemClass      单条数据class类型
     * @return 数据对象
     */
    public T findOne(M mapper, Long id, Long cacheTimestamp, Class<T> itemClass) {
        return StorageManager.getInstance().findOne(mapper, id, cacheTimestamp, itemClass);
    }

    /**
     * 查询单条数据
     *
     * @param mapper       mapper
     * @param queryWrapper 查询条件
     * @return 数据对象
     */
    public T findOne(M mapper, QueryWrapper<T> queryWrapper) {
        return StorageManager.getInstance().findOne(mapper, queryWrapper);
    }

    /**
     * 查询单条数据
     *
     * @param mapper mapper
     * @param id     数据id
     * @return 数据对象
     */
    public T findOne(M mapper, Long id) {
        return StorageManager.getInstance().findOne(mapper, id);
    }

    /**
     * 插入或更新数据,同时删除相应的缓存数据
     *
     * @param mapper mapper
     * @param entity 数据实体
     * @return 数据id
     */
    public Long insertOrUpdate(M mapper, T entity) {
        return StorageManager.getInstance().insertOrUpdate(mapper, entity);
    }

    /**
     * 更新数据,同时删除相应的缓存数据
     *
     * @param mapper mapper
     * @param entity 数据实体
     * @return 数据id
     */
    public Long updateByPrimaryKeySelective(M mapper, T entity) {
        return StorageManager.getInstance().updateByPrimaryKeySelective(mapper, entity);
    }

    /**
     * 删除数据,同时删除相应的缓存数据
     *
     * @param mapper    mapper
     * @param primaryId 主键id
     * @param itemClass 数据class类型
     */
    public void delete(M mapper, Long primaryId, Class<T> itemClass) {
        StorageManager.getInstance().delete(mapper, primaryId, itemClass);
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
    public <Item, Mapper> PageListResponse<List<Item>> getGenericPageList(Mapper mapper, PageRequest request, QueryWrapper queryWrapper, Class<Item> itemClass, DbMethodEntry methodEntry, boolean skipCache) {
        return StorageManager.getInstance().getGenericPageList(mapper, request, queryWrapper, itemClass, methodEntry, skipCache);
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
        return StorageManager.getInstance().getPageList(mapper, request, queryWrapper, itemClass, methodEntry, skipCache);
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
        return StorageManager.getInstance().getPageList(mapper, request, queryWrapper, itemClass, methodEntry);
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
    public <Item, Mapper> PageListResponse<List<Item>> getGenericPageList(Mapper mapper, PageRequest request, QueryWrapper queryWrapper, Class<Item> itemClass, DbMethodEntry methodEntry) {
        return StorageManager.getInstance().getGenericPageList(mapper, request, queryWrapper, itemClass, methodEntry);
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
        return StorageManager.getInstance().getPageList(mapper, request, itemClass, methodEntry);
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
    public <Item, Mapper> PageListResponse<List<Item>> getGenericPageList(Mapper mapper, PageRequest request, Class<Item> itemClass, DbMethodEntry methodEntry) {
        return StorageManager.getInstance().getGenericPageList(mapper, request, itemClass, methodEntry);
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
        return StorageManager.getInstance().getPageList(mapper, page, limit, queryWrapper, itemClass, methodEntry, skipCache);
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
    public <Item, Mapper> PageListResponse<List<Item>> getGenericPageList(Mapper mapper, int page, int limit, QueryWrapper queryWrapper, Class<Item> itemClass, DbMethodEntry methodEntry, boolean skipCache) {
        return StorageManager.getInstance().getGenericPageList(mapper, page, limit, queryWrapper, itemClass, methodEntry, skipCache);
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
        return StorageManager.getInstance().getPageList(mapper, page, limit, itemClass, methodEntry, queryWrapper);
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
    public <Item, Mapper> PageListResponse<List<Item>> getGenericPageList(Mapper mapper, int page, int limit, Class<Item> itemClass, DbMethodEntry methodEntry, QueryWrapper queryWrapper) {
        return StorageManager.getInstance().getGenericPageList(mapper, page, limit, itemClass, methodEntry, queryWrapper);
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
        return StorageManager.getInstance().getPageList(mapper, page, limit, itemClass, methodEntry);
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
    public <Item, Mapper> PageListResponse<List<Item>> getGenericPageList(Mapper mapper, int page, int limit, Class<Item> itemClass, DbMethodEntry methodEntry) {
        return StorageManager.getInstance().getGenericPageList(mapper, page, limit, itemClass, methodEntry);
    }

    public <T extends BaseEntity, M extends ItemMapper<T>> boolean isExist(M mapper, QueryWrapper queryWrapper) {
        return StorageManager.getInstance().isExist(mapper, queryWrapper);
    }

    public <Item extends BaseEntity> boolean isExist(Item entity) {
        return StorageManager.getInstance().isExist(entity);
    }

    public <Item extends BaseEntity> boolean isNotExist(Item entity) {
        return StorageManager.getInstance().isNotExist(entity);
    }
}
