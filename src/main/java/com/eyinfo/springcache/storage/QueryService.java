package com.eyinfo.springcache.storage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.encrypts.MD5Encrypt;
import com.eyinfo.foundation.entity.BaseEntity;
import com.eyinfo.foundation.utils.*;
import com.eyinfo.springcache.storage.entity.PageConditions;
import com.eyinfo.springcache.storage.entity.QueryConditions;
import com.eyinfo.springcache.storage.entity.SearchCondition;
import com.eyinfo.springcache.storage.events.ModelConditions;
import com.eyinfo.springcache.storage.events.OnCacheStrategy;
import com.eyinfo.springcache.storage.invoke.InvokeResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/6/24
 * @Description:
 * @Modifier:
 * @ModifyContent:
 */
class QueryService extends BaseService {

    public <R, Dao, T> PageInfo<R> select(Dao dao, Class<R> itemClass, DbMethodEntry methodEntry, PageConditions<T> conditions, OnCacheStrategy<SearchCondition, PageInfo<R>, R> cacheStrategy) {
        SearchCondition searchCondition = new SearchCondition();
        QueryWrapper<T> queryWrapper = conditions.getQueryWrapper();
        searchCondition.setQueryWrapper(queryWrapper);
        searchCondition.setPageNumber(Math.max(conditions.getPageNumber(), 1));
        searchCondition.setPageSize(conditions.getPageSize() <= 0 ? 30 : conditions.getPageSize());
        if (queryWrapper == null) {
            if (conditions.isMap()) {
                StringBuilder builder = new StringBuilder();
                if (!TextUtils.isEmpty(conditions.getPrecondition())) {
                    builder.append(conditions.getPrecondition());
                }
                if (!TextUtils.isEmpty(conditions.getOrderBy())) {
                    builder.append(" ").append(conditions.getOrderBy());
                }
                searchCondition.setConditionSql(builder.toString());
                searchCondition.setParams(conditions.getConditions());
            } else {
                HashMap<String, Object> conditionsMap = conditions.getConditions();
                searchCondition.setConditionSql(tranConditionSql(conditions.getPrecondition(), conditionsMap, conditions.getOrderBy()));
            }
        }
        if (cacheStrategy != null) {
            PageInfo<R> pageInfo = cacheStrategy.onQueryCache(methodEntry, searchCondition, itemClass);
            if (pageInfo != null) {
                List<R> list = pageInfo.getList();
                if (!ObjectJudge.isNullOrEmpty(list) && TextUtils.equals(list.get(0).getClass().getSimpleName(), itemClass.getSimpleName())) {
                    return pageInfo;
                }
            }
        }
        return queryDbPageInfo(dao, methodEntry, searchCondition, cacheStrategy, conditions.isMap());
    }

    private <R, Dao, T> PageInfo<R> queryDbPageInfo(Dao dao, DbMethodEntry methodEntry, SearchCondition searchCondition, OnCacheStrategy<SearchCondition, PageInfo<R>, R> cacheStrategy, boolean isMap) {
        PageHelper.startPage(searchCondition.getPageNumber(), searchCondition.getPageSize(), true, true, false);
        Object result;
        QueryWrapper<T> queryWrapper = searchCondition.getQueryWrapper();
        if (queryWrapper == null) {
            if (isMap) {
                result = selectFromDB(dao, methodEntry, searchCondition.getParams());
            } else {
                result = selectFromDB(dao, methodEntry, searchCondition);
            }
        } else {
            result = selectPlusFromDB(dao, methodEntry, searchCondition.getQueryWrapper());
        }
        PageInfo<R> pageInfo = new PageInfo<R>((result instanceof List) ? (List) result : new LinkedList<>());
        if (cacheStrategy != null) {
            cacheStrategy.onDataCache(methodEntry, searchCondition, pageInfo);
        }
        return pageInfo;
    }

    public <R, Dao> List<R> select(Dao dao, Class<R> itemClass, DbMethodEntry methodEntry, QueryConditions conditions, OnCacheStrategy<String, List<R>, R> cacheStrategy) {
        if (cacheStrategy != null) {
            String conditionSql = tranConditionSql(conditions.getPrecondition(), conditions.getConditions(), conditions.getOrderBy());
            List<R> list = cacheStrategy.onQueryCache(methodEntry, conditionSql, itemClass);
            if (!ObjectJudge.isNullOrEmpty(list) && TextUtils.equals(list.get(0).getClass().getSimpleName(), itemClass.getSimpleName())) {
                return list;
            }
        }
        return queryList(dao, methodEntry, conditions, cacheStrategy);
    }

    public <R, Dao> List<R> select(Dao dao, Class<R> itemClass, DbMethodEntry methodEntry, List<String> listParams, OnCacheStrategy<String, List<R>, R> cacheStrategy) {
        if (cacheStrategy != null) {
            String condition = MD5Encrypt.md5(ConvertUtils.toString(listParams.hashCode()));
            List<R> list = cacheStrategy.onQueryCache(methodEntry, condition, itemClass);
            if (!ObjectJudge.isNullOrEmpty(list) && TextUtils.equals(list.get(0).getClass().getSimpleName(), itemClass.getSimpleName())) {
                return list;
            }
        }
        return queryList(dao, methodEntry, listParams, cacheStrategy);
    }

    public <R, Dao> List<R> select(Dao dao, Class<R> itemClass, DbMethodEntry methodEntry, HashMap<String, Object> mapParams, OnCacheStrategy<String, List<R>, R> cacheStrategy) {
        if (cacheStrategy != null) {
            String condition = MD5Encrypt.md5(ConvertUtils.toString(mapParams.hashCode()));
            List<R> list = cacheStrategy.onQueryCache(methodEntry, condition, itemClass);
            if (!ObjectJudge.isNullOrEmpty(list) && TextUtils.equals(list.get(0).getClass().getSimpleName(), itemClass.getSimpleName())) {
                return list;
            }
        }
        return queryList(dao, methodEntry, mapParams, cacheStrategy);
    }

    public <R, Dao> List<R> selectPlus(Dao dao, Class itemClass, DbMethodEntry methodEntry, QueryWrapper queryWrapper, OnCacheStrategy<QueryWrapper, List<R>, R> cacheStrategy) {
        if (cacheStrategy != null) {
            List<R> list = cacheStrategy.onQueryCache(methodEntry, queryWrapper, itemClass);
            if (!ObjectJudge.isNullOrEmpty(list) && TextUtils.equals(list.get(0).getClass().getSimpleName(), itemClass.getSimpleName())) {
                return list;
            }
        }
        return queryListPlus(dao, methodEntry, queryWrapper, cacheStrategy);
    }

    private <R, Dao> List<R> queryList(Dao dao, DbMethodEntry methodEntry, QueryConditions conditions, OnCacheStrategy<String, List<R>, R> cacheStrategy) {
        String conditionSql = tranConditionSql(conditions.getPrecondition(), conditions.getConditions(), conditions.getOrderBy());
        Object result = selectListFromDB(dao, methodEntry, conditionSql);
        if (cacheStrategy != null) {
            cacheStrategy.onDataCache(methodEntry, conditionSql, result);
        }
        return (result instanceof List) ? (List<R>) result : null;
    }

    private <R, Dao> List<R> queryList(Dao dao, DbMethodEntry methodEntry, List<String> listParams, OnCacheStrategy<String, List<R>, R> cacheStrategy) {
        Object result = selectListFromDB(dao, methodEntry, listParams);
        if (cacheStrategy != null) {
            String condition = MD5Encrypt.md5(ConvertUtils.toString(listParams.hashCode()));
            cacheStrategy.onDataCache(methodEntry, condition, result);
        }
        return (result instanceof List) ? (List<R>) result : null;
    }

    private <R, Dao> List<R> queryList(Dao dao, DbMethodEntry methodEntry, HashMap<String, Object> mapParams, OnCacheStrategy<String, List<R>, R> cacheStrategy) {
        Object result = selectListFromDB(dao, methodEntry, mapParams);
        if (cacheStrategy != null) {
            String condition = MD5Encrypt.md5(ConvertUtils.toString(mapParams.hashCode()));
            cacheStrategy.onDataCache(methodEntry, condition, result);
        }
        return (result instanceof List) ? (List<R>) result : null;
    }

    private <R, Dao> List<R> queryListPlus(Dao dao, DbMethodEntry methodEntry, QueryWrapper queryWrapper, OnCacheStrategy<QueryWrapper, List<R>, R> cacheStrategy) {
        List<R> result = selectListPlusFromDB(dao, methodEntry, queryWrapper);
        if (cacheStrategy != null) {
            cacheStrategy.onDataCache(methodEntry, queryWrapper, result);
        }
        return (result instanceof List) ? result : null;
    }

    private String tranConditionSql(String precondition, HashMap<String, Object> conditionsMap, String orderBy) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : conditionsMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Integer || value instanceof Double || value instanceof Float || value instanceof Long || value instanceof Short) {
                if (builder.length() > 0) {
                    builder.append(" and ").append(entry.getKey()).append(" = ").append(value);
                } else {
                    builder.append(" ").append(entry.getKey()).append(" = ").append(value);
                }
            } else {
                builder.append(builder.length() > 0 ? " and " : " ");
                String content = ConvertUtils.toString(value);
                String match = ValidUtils.match("(in)(\\s)?(\\()", content).replace(" ", "");
                if (match.startsWith("in(")) {
                    builder.append(entry.getKey()).append(content);
                } else {
                    builder.append(entry.getKey()).append(" = '").append(content).append("'");
                }
            }
        }
        if (!TextUtils.isEmpty(precondition)) {
            builder.insert(0, precondition);
        }
        if (!TextUtils.isEmpty(orderBy)) {
            builder.append(" ").append(orderBy);
        }
        return builder.toString();
    }

    public <R extends BaseEntity, Dao, C extends ModelConditions> R select(Dao dao, Class<R> entityClass, DbMethodEntry methodEntry, C conditions, OnCacheStrategy<C, R, R> cacheStrategy, boolean skipCache) {
        if (TextUtils.isEmpty(methodEntry.getMethodName())) {
            methodEntry.setMethodName("find");
        }
        if (cacheStrategy != null && !skipCache) {
            R cache = cacheStrategy.onQueryCache(methodEntry, conditions, entityClass);
            if (cache != null && cache.getClass() == entityClass) {
                String id = ConvertUtils.toString(GlobalUtils.getPropertiesValue(cache, "id"));
                if (!TextUtils.isEmpty(id)) {
                    return cache;
                }
            }
        }
        return queryDbData(dao, methodEntry, conditions, cacheStrategy);
    }

    public <Dao, C extends ModelConditions> String select(Dao dao, DbMethodEntry methodEntry, C conditions, OnCacheStrategy<C, String, String> cacheStrategy, boolean skipCache) {
        if (TextUtils.isEmpty(methodEntry.getMethodName())) {
            methodEntry.setMethodName("find");
        }
        if (cacheStrategy != null && !skipCache) {
            return cacheStrategy.onQueryCache(methodEntry, conditions, null);
        }
        return queryStringDbData(dao, methodEntry, conditions, cacheStrategy);
    }

    private <Dao, C extends ModelConditions, R extends BaseEntity> R queryDbData(Dao dao, DbMethodEntry methodEntry, C conditions, OnCacheStrategy<C, R, R> cacheStrategy) {
        QueryWrapper queryWrapper = conditions.getQueryWrapper();
        R select;
        if (queryWrapper == null) {
            select = selectFromDB(dao, methodEntry, conditions.getWhere());
        } else {
            select = selectFromDB(dao, methodEntry, queryWrapper);
        }
        if (cacheStrategy != null) {
            cacheStrategy.onDataCache(methodEntry, conditions, select);
        }
        return select;
    }

    private <Dao, C extends ModelConditions> String queryStringDbData(Dao dao, DbMethodEntry methodEntry, C conditions, OnCacheStrategy<C, String, String> cacheStrategy) {
        QueryWrapper queryWrapper = conditions.getQueryWrapper();
        String select;
        if (queryWrapper == null) {
            select = selectStringFromDB(dao, methodEntry, conditions.getWhere());
        } else {
            select = selectStringFromDB(dao, methodEntry, queryWrapper);
        }
        if (cacheStrategy != null) {
            cacheStrategy.onDataCache(methodEntry, conditions, select);
        }
        return select;
    }

    public <R extends BaseEntity, Dao, C extends ModelConditions> R select(Dao dao, DbMethodEntry methodEntry, Class<R> entityClass, C conditions, OnCacheStrategy<C, R, R> cacheStrategy) {
        return select(dao, entityClass, methodEntry, conditions, cacheStrategy, false);
    }

    private <R, Dao> R selectFromDB(Dao dao, DbMethodEntry methodEntry, SearchCondition conditions) {
        InvokeResult invokeResult = super.invokeWithString(dao, methodEntry, conditions.getConditionSql());
        if (!invokeResult.isSuccess()) {
            return null;
        }
        return (R) invokeResult.getResult();
    }

    private <R, Dao, T> R selectPlusFromDB(Dao dao, DbMethodEntry methodEntry, QueryWrapper<T> queryWrapper) {
        InvokeResult invokeResult = super.invoke(dao, methodEntry, queryWrapper);
        if (!invokeResult.isSuccess()) {
            return null;
        }
        return (R) invokeResult.getResult();
    }

    private <R, Dao> R selectFromDB(Dao dao, DbMethodEntry methodEntry, HashMap<String, Object> params) {
        InvokeResult invokeResult = super.invoke(dao, methodEntry, params);
        if (!invokeResult.isSuccess()) {
            return null;
        }
        return (R) invokeResult.getResult();
    }

    private <Dao, T extends BaseEntity> T selectFromDB(Dao dao, DbMethodEntry methodEntry, String where) {
        InvokeResult invokeResult = super.invokeWithString(dao, methodEntry, where);
        if (!invokeResult.isSuccess()) {
            return null;
        }
        return (T) invokeResult.getResult();
    }

    private <Dao, T extends BaseEntity> T selectFromDB(Dao dao, DbMethodEntry methodEntry, QueryWrapper queryWrapper) {
        InvokeResult invokeResult = super.invoke(dao, methodEntry, queryWrapper);
        if (!invokeResult.isSuccess()) {
            return null;
        }
        return (T) invokeResult.getResult();
    }

    private <Dao> String selectStringFromDB(Dao dao, DbMethodEntry methodEntry, String where) {
        InvokeResult invokeResult = super.invokeWithString(dao, methodEntry, where);
        if (!invokeResult.isSuccess()) {
            return null;
        }
        return (String) invokeResult.getResult();
    }

    private <Dao, T> String selectStringFromDB(Dao dao, DbMethodEntry methodEntry, QueryWrapper<T> queryWrapper) {
        InvokeResult invokeResult = super.invoke(dao, methodEntry, queryWrapper);
        if (!invokeResult.isSuccess()) {
            return null;
        }
        return (String) invokeResult.getResult();
    }

    private <Dao, T extends BaseEntity> List<T> selectListFromDB(Dao dao, DbMethodEntry methodEntry, String where) {
        InvokeResult invokeResult = super.invokeWithString(dao, methodEntry, where);
        if (!invokeResult.isSuccess()) {
            return null;
        }
        return (List<T>) invokeResult.getResult();
    }

    private <Dao, T extends BaseEntity> List<T> selectListFromDB(Dao dao, DbMethodEntry methodEntry, List<String> listParams) {
        InvokeResult invoke = super.invoke(dao, methodEntry, listParams);
        if (!invoke.isSuccess()) {
            return null;
        }
        return (List<T>) invoke.getResult();
    }

    private <Dao, T extends BaseEntity> List<T> selectListFromDB(Dao dao, DbMethodEntry methodEntry, HashMap<String, Object> mapParams) {
        InvokeResult invoke = super.invoke(dao, methodEntry, mapParams);
        if (!invoke.isSuccess()) {
            return null;
        }
        return (List<T>) invoke.getResult();
    }

    private <Dao, T> List<T> selectListPlusFromDB(Dao dao, DbMethodEntry methodEntry, QueryWrapper queryWrapper) {
        InvokeResult invoke = super.invoke(dao, methodEntry, queryWrapper);
        if (!invoke.isSuccess()) {
            return null;
        }
        return (List<T>) invoke.getResult();
    }

    public void removeCache(DbMethodEntry methodEntry, String where, OnCacheStrategy<String, Void, Void> cacheStrategy) {
        if (TextUtils.isEmpty(methodEntry.getMethodName())) {
            methodEntry.setMethodName("find");
        }
        if (cacheStrategy != null) {
            cacheStrategy.onRemoveCache(methodEntry, where);
        }
    }
}
