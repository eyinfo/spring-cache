package com.eyinfo.springcache.storage;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.entity.BaseEntity;
import com.eyinfo.foundation.entity.PageListResponse;
import com.eyinfo.foundation.utils.TimeSyncUtils;
import com.eyinfo.springcache.entity.CachingStrategyConfig;
import com.eyinfo.springcache.mongo.MongoManager;
import com.eyinfo.springcache.response.EyResult;
import com.eyinfo.springcache.storage.entity.PageConditions;
import com.eyinfo.springcache.storage.entity.PageRequest;
import com.eyinfo.springcache.storage.mybatis.ItemMapper;
import com.github.pagehelper.PageInfo;

import java.util.Collections;
import java.util.List;

public class WithService {

    private <T extends BaseEntity, M extends ItemMapper<T>> T findDataFromDb(M mapper, QueryWrapper<T> queryWrapper) {
        String sqlSegment = queryWrapper.getSqlSegment();
        if (!sqlSegment.contains("limit")) {
            queryWrapper.last("limit 1");
        }
        return mapper.getDataPlus(queryWrapper);
    }

    private String getCacheKey(QueryWrapper queryWrapper, String cachePrefix) {
        StringBuilder builder = new StringBuilder(cachePrefix);
        builder.append(KeysStorage.combQueryWrapper(queryWrapper));
        return builder.toString();
    }

    /**
     * 查询单条数据
     *
     * @param mapper         mapper
     * @param queryWrapper   查询条件
     * @param cacheTimestamp 缓存时间戳
     * @param itemClass      单条数据class类型
     * @return 数据对象
     */
    public <T extends BaseEntity, M extends ItemMapper<T>> T findOne(M mapper, QueryWrapper<T> queryWrapper, Long cacheTimestamp, Class<T> itemClass) {
        TableName declaredAnnotation = itemClass.getDeclaredAnnotation(TableName.class);
        String tableName = declaredAnnotation.value();
        String key = getCacheKey(queryWrapper, tableName);
        T data = MongoManager.getInstance().get(key, itemClass, false);
        if (data == null || data.getId() == null || data.getId() <= 0) {
            T dataFromDb = findDataFromDb(mapper, queryWrapper);
            if (dataFromDb == null || dataFromDb.getId() == null || dataFromDb.getId() <= 0) {
                return null;
            }
            if (cacheTimestamp == null || cacheTimestamp <= 0) {
                CachingStrategyConfig strategyConfig = StorageUtils.getCachingStrategyConfig();
                MongoManager.getInstance().save(key, dataFromDb, strategyConfig.getApiGlobalCacheTime());
            } else {
                MongoManager.getInstance().save(key, dataFromDb, cacheTimestamp);
            }
            return dataFromDb;
        }
        return data;
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
    public <T extends BaseEntity, M extends ItemMapper<T>> T findOne(M mapper, Long id, Long cacheTimestamp, Class<T> itemClass) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return findOne(mapper, queryWrapper, cacheTimestamp, itemClass);
    }

    /**
     * 查询单条数据
     *
     * @param mapper       mapper
     * @param queryWrapper 查询条件
     * @return 数据对象
     */
    public <T extends BaseEntity, M extends ItemMapper<T>> T findOne(M mapper, QueryWrapper<T> queryWrapper) {
        return findDataFromDb(mapper, queryWrapper);
    }

    /**
     * 查询单条数据
     *
     * @param mapper mapper
     * @param id     数据id
     * @return 数据对象
     */
    public <T extends BaseEntity, M extends ItemMapper<T>> T findOne(M mapper, Long id) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return findDataFromDb(mapper, queryWrapper);
    }

    /**
     * 插入或更新数据,同时删除相应的缓存数据
     *
     * @param mapper mapper
     * @param entity 数据实体
     * @return 数据id
     */
    public <T extends BaseEntity, M extends ItemMapper<T>> Long insertOrUpdate(M mapper, T entity) {
        entity.setModifyTime(TimeSyncUtils.getUTCTimestamp());
        if (entity.getId() == null || entity.getId() == 0) {
            //自增id会自动填充到entity中
            entity.setCreateTime(TimeSyncUtils.getUTCTimestamp());
            mapper.insertSelective(entity);
        } else {
            mapper.updateByPrimaryKeySelective(entity);
        }
        Class<? extends BaseEntity> entityClass = entity.getClass();
        TableName declaredAnnotation = entityClass.getDeclaredAnnotation(TableName.class);
        String tableName = declaredAnnotation.value();
        MongoManager.getInstance().blurDelete(Collections.singletonList(tableName));
        return entity.getId();
    }

    /**
     * 更新数据,同时删除相应的缓存数据
     *
     * @param mapper mapper
     * @param entity 数据实体
     * @return 数据id
     */
    public <T extends BaseEntity, M extends ItemMapper<T>> Long updateByPrimaryKeySelective(M mapper, T entity) {
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(TimeSyncUtils.getUTCTimestamp());
        }
        entity.setModifyTime(TimeSyncUtils.getUTCTimestamp());
        mapper.updateByPrimaryKeySelective(entity);
        Class<? extends BaseEntity> entityClass = entity.getClass();
        TableName declaredAnnotation = entityClass.getDeclaredAnnotation(TableName.class);
        String tableName = declaredAnnotation.value();
        MongoManager.getInstance().blurDelete(Collections.singletonList(tableName));
        return entity.getId();
    }

    /**
     * 删除数据,同时删除相应的缓存数据
     *
     * @param mapper    mapper
     * @param primaryId 主键id
     * @param itemClass 数据class类型
     */
    public <T extends BaseEntity, M extends ItemMapper<T>> void delete(M mapper, Long primaryId, Class<T> itemClass) {
        mapper.deleteByPrimaryKey(primaryId);
        TableName declaredAnnotation = itemClass.getDeclaredAnnotation(TableName.class);
        String tableName = declaredAnnotation.value();
        MongoManager.getInstance().blurDelete(Collections.singletonList(tableName));
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
        PageInfo<Item> pageInfo = StorageManager.getInstance().queryPage(mapper, itemClass, methodEntry, conditions, skipCache);
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
     * @param skipCache    是否跳过缓存
     * @return
     */
    public <T extends BaseEntity, M extends ItemMapper<T>> PageListResponse<List<T>> getPageList(M mapper, PageRequest request, QueryWrapper queryWrapper, Class<T> itemClass, DbMethodEntry methodEntry, boolean skipCache) {
        return this.getGenericPageList(mapper, request, queryWrapper, itemClass, methodEntry, skipCache);
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
    public <T extends BaseEntity, M extends ItemMapper<T>> PageListResponse<List<T>> getPageList(M mapper, PageRequest request, QueryWrapper queryWrapper, Class<T> itemClass, DbMethodEntry methodEntry) {
        return this.getPageList(mapper, request, queryWrapper, itemClass, methodEntry, false);
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
        return this.getGenericPageList(mapper, request, queryWrapper, itemClass, methodEntry, false);
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
    public <T extends BaseEntity, M extends ItemMapper<T>> PageListResponse<List<T>> getPageList(M mapper, PageRequest request, Class<T> itemClass, DbMethodEntry methodEntry) {
        return this.getPageList(mapper, request, null, itemClass, methodEntry, false);
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
        return this.getGenericPageList(mapper, request, null, itemClass, methodEntry, false);
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
    public <T extends BaseEntity, M extends ItemMapper<T>> PageListResponse<List<T>> getPageList(M mapper, int page, int limit, QueryWrapper queryWrapper, Class<T> itemClass, DbMethodEntry methodEntry, boolean skipCache) {
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
     * @param queryWrapper 查询条件
     * @param itemClass    单条数据class类型
     * @param methodEntry  查询缓存定义
     * @param skipCache    是否跳过缓存
     * @return
     */
    public <Item, Mapper> PageListResponse<List<Item>> getGenericPageList(Mapper mapper, int page, int limit, QueryWrapper queryWrapper, Class<Item> itemClass, DbMethodEntry methodEntry, boolean skipCache) {
        PageRequest request = new PageRequest();
        request.setPage(page);
        request.setLimit(limit);
        return this.getGenericPageList(mapper, request, queryWrapper, itemClass, methodEntry, skipCache);
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
    public <T extends BaseEntity, M extends ItemMapper<T>> PageListResponse<List<T>> getPageList(M mapper, int page, int limit, Class<T> itemClass, DbMethodEntry methodEntry, QueryWrapper queryWrapper) {
        return this.getPageList(mapper, page, limit, queryWrapper, itemClass, methodEntry, false);
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
        return this.getGenericPageList(mapper, page, limit, queryWrapper, itemClass, methodEntry, false);
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
    public <T extends BaseEntity, M extends ItemMapper<T>> PageListResponse<List<T>> getPageList(M mapper, int page, int limit, Class<T> itemClass, DbMethodEntry methodEntry) {
        return this.getPageList(mapper, page, limit, null, itemClass, methodEntry, false);
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
        return this.getGenericPageList(mapper, page, limit, null, itemClass, methodEntry, false);
    }

    public <T extends BaseEntity, M extends ItemMapper<T>> boolean isExist(M mapper, QueryWrapper queryWrapper) {
        return mapper.countPlus(queryWrapper) > 0;
    }

    public <Item extends BaseEntity> boolean isExist(Item entity) {
        return entity != null && entity.getId() > 0;
    }

    public <Item extends BaseEntity> boolean isNotExist(Item entity) {
        return entity == null || entity.getId() <= 0;
    }
}
