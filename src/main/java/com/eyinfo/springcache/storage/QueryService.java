package com.eyinfo.springcache.storage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.utils.ConvertUtils;
import com.eyinfo.foundation.utils.ObjectJudge;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.foundation.utils.ValidUtils;
import com.eyinfo.springcache.storage.entity.PageConditions;
import com.eyinfo.springcache.storage.entity.QueryConditions;
import com.eyinfo.springcache.storage.entity.SearchCondition;
import com.eyinfo.springcache.storage.enums.Methods;
import com.eyinfo.springcache.storage.events.ModelConditions;
import com.eyinfo.springcache.storage.events.OnCacheStrategy;
import com.eyinfo.springcache.storage.invoke.InvokeResult;
import com.eyinfo.springcache.storage.mybatis.BaseMapper;
import com.eyinfo.springcache.storage.mybatis.PrototypeMapper;
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

    public <Item, Dao extends PrototypeMapper<Item>> PageInfo<Item> select(Dao dao, Class<Item> itemClass, DbMethodEntry methodEntry, PageConditions conditions, OnCacheStrategy<SearchCondition, PageInfo<Item>, Item> cacheStrategy) {
        SearchCondition searchCondition = new SearchCondition();
        QueryWrapper queryWrapper = conditions.getQueryWrapper();
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
            PageInfo<Item> pageInfo = cacheStrategy.onQueryCache(methodEntry, searchCondition, itemClass);
            if (pageInfo != null) {
                List<Item> list = pageInfo.getList();
                if (!ObjectJudge.isNullOrEmpty(list) && TextUtils.equals(list.get(0).getClass().getSimpleName(), itemClass.getSimpleName())) {
                    return pageInfo;
                }
            }
        }
        return queryDbPageInfo(dao, methodEntry, searchCondition, cacheStrategy, conditions.isMap());
    }

    private <Item, Dao extends PrototypeMapper<Item>> PageInfo<Item> queryDbPageInfo(Dao dao, DbMethodEntry methodEntry, SearchCondition searchCondition, OnCacheStrategy<SearchCondition, PageInfo<Item>, Item> cacheStrategy, boolean isMap) {
        PageHelper.startPage(searchCondition.getPageNumber(), searchCondition.getPageSize(), true, true, false);
        List<Item> result;
        QueryWrapper queryWrapper = searchCondition.getQueryWrapper();
        if (queryWrapper == null || !TextUtils.equals(methodEntry.getMethodName(), Methods.getListPlus.name())) {
            if (isMap) {
                result = selectFromDB(dao, methodEntry, searchCondition.getParams());
            } else {
                result = selectFromDB(dao, methodEntry, searchCondition);
            }
        } else {
            if (dao instanceof BaseMapper) {
                result = ((BaseMapper<?>) dao).getListPlus(queryWrapper);
            } else {
                result = selectPlusFromDB(dao, methodEntry, searchCondition.getQueryWrapper());
            }
        }
        PageInfo<Item> pageInfo = new PageInfo<>((result == null) ? result : new LinkedList<>());
        if (cacheStrategy != null) {
            cacheStrategy.onDataCache(methodEntry, searchCondition, pageInfo);
        }
        return pageInfo;
    }

    public <Item, Dao extends PrototypeMapper<Item>> List<Item> select(Dao dao, Class<Item> itemClass, DbMethodEntry methodEntry, QueryConditions conditions, OnCacheStrategy<String, List<Item>, Item> cacheStrategy) {
        String conditionSql = tranConditionSql(conditions.getPrecondition(), conditions.getConditions(), conditions.getOrderBy());
        if (cacheStrategy == null) {
            return selectListFromDB(dao, methodEntry, conditionSql);
        } else {
            List<Item> list = cacheStrategy.onQueryCache(methodEntry, conditionSql, itemClass);
            if (!ObjectJudge.isNullOrEmpty(list) && TextUtils.equals(list.get(0).getClass().getSimpleName(), itemClass.getSimpleName())) {
                return list;
            }
            List<Item> result = selectListFromDB(dao, methodEntry, conditionSql);
            cacheStrategy.onDataCache(methodEntry, conditionSql, result);
            return result;
        }
    }

    public <Item, Dao extends PrototypeMapper<Item>> List<Item> selectPlus(Dao dao, Class<Item> itemClass, DbMethodEntry methodEntry, QueryWrapper queryWrapper, OnCacheStrategy<QueryWrapper, List<Item>, Item> cacheStrategy) {
        if (cacheStrategy == null) {
            return selectListPlusFromDB(dao, methodEntry, queryWrapper);
        } else {
            List<Item> list = cacheStrategy.onQueryCache(methodEntry, queryWrapper, itemClass);
            if (!ObjectJudge.isNullOrEmpty(list) && TextUtils.equals(list.get(0).getClass().getSimpleName(), itemClass.getSimpleName())) {
                return list;
            }
            List<Item> result = selectListPlusFromDB(dao, methodEntry, queryWrapper);
            cacheStrategy.onDataCache(methodEntry, queryWrapper, result);
            return result;
        }
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

    public <Item, Dao extends PrototypeMapper<Item>, C extends ModelConditions> Item select(Dao dao, Class<Item> entityClass, DbMethodEntry methodEntry, C conditions, OnCacheStrategy<C, Item, Item> cacheStrategy, boolean skipCache) {
        if (cacheStrategy != null && !skipCache) {
            Item cache = cacheStrategy.onQueryCache(methodEntry, conditions, entityClass);
            if (cache != null && cache.getClass() == entityClass) {
                return cache;
            }
        }
        QueryWrapper queryWrapper = conditions.getQueryWrapper();
        Item select;
        if (queryWrapper == null) {
            select = selectFromDB(dao, methodEntry, conditions.getWhere());
        } else {
            select = selectFromDB(dao, methodEntry, queryWrapper);
        }
        if (cacheStrategy != null && !skipCache) {
            cacheStrategy.onDataCache(methodEntry, conditions, select);
        }
        return select;
    }

    public <Dao, C extends ModelConditions> String select(Dao dao, DbMethodEntry methodEntry, C conditions, OnCacheStrategy<C, String, String> cacheStrategy, boolean skipCache) {
        if (cacheStrategy != null && !skipCache) {
            return cacheStrategy.onQueryCache(methodEntry, conditions, null);
        }
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

    private <R, Dao> R selectFromDB(Dao dao, DbMethodEntry methodEntry, SearchCondition conditions) {
        InvokeResult invokeResult = super.invokeWithString(dao, methodEntry, conditions.getConditionSql());
        if (!invokeResult.isSuccess()) {
            return null;
        }
        return (R) invokeResult.getResult();
    }

    private <R, Dao> R selectPlusFromDB(Dao dao, DbMethodEntry methodEntry, QueryWrapper queryWrapper) {
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

    private <Dao extends PrototypeMapper<T>, T> T selectFromDB(Dao dao, DbMethodEntry methodEntry, String where) {
        if (dao instanceof BaseMapper && TextUtils.equals(methodEntry.getMethodName(), Methods.getDataPlus.name())) {
            QueryWrapper queryWrapper = new QueryWrapper<>();
            queryWrapper.last(where);
            return (T) ((BaseMapper<T>) dao).getDataPlus(queryWrapper);
        } else {
            InvokeResult invokeResult = super.invokeWithString(dao, methodEntry, where);
            if (!invokeResult.isSuccess()) {
                return null;
            }
            return (T) invokeResult.getResult();
        }
    }

    private <Dao extends PrototypeMapper<T>, T> T selectFromDB(Dao dao, DbMethodEntry methodEntry, QueryWrapper queryWrapper) {
        if (dao instanceof BaseMapper && TextUtils.equals(methodEntry.getMethodName(), Methods.getDataPlus.name())) {
            return (T) ((BaseMapper<T>) dao).getDataPlus(queryWrapper);
        } else {
            InvokeResult invokeResult = super.invoke(dao, methodEntry, queryWrapper);
            if (!invokeResult.isSuccess()) {
                return null;
            }
            return (T) invokeResult.getResult();
        }
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

    private <Dao extends PrototypeMapper<T>, T> List<T> selectListFromDB(Dao dao, DbMethodEntry methodEntry, String where) {
        if (dao instanceof BaseMapper && TextUtils.equals(methodEntry.getMethodName(), Methods.getListPlus.name())) {
            QueryWrapper queryWrapper = new QueryWrapper<>();
            queryWrapper.last(where);
            return ((BaseMapper<T>) dao).getListPlus(queryWrapper);
        } else {
            InvokeResult invokeResult = super.invokeWithString(dao, methodEntry, where);
            if (!invokeResult.isSuccess()) {
                return null;
            }
            return (List<T>) invokeResult.getResult();
        }
    }

    private <Dao extends PrototypeMapper<T>, T> List<T> selectListPlusFromDB(Dao dao, DbMethodEntry methodEntry, QueryWrapper queryWrapper) {
        if (dao instanceof BaseMapper && TextUtils.equals(methodEntry.getMethodName(), Methods.getListPlus.name())) {
            return ((BaseMapper<T>) dao).getListPlus(queryWrapper);
        } else {
            InvokeResult invoke = super.invoke(dao, methodEntry, queryWrapper);
            if (!invoke.isSuccess()) {
                return null;
            }
            return (List<T>) invoke.getResult();
        }
    }

    public void removeCache(DbMethodEntry methodEntry, String where, OnCacheStrategy<String, Void, Void> cacheStrategy) {
        if (cacheStrategy != null) {
            cacheStrategy.onRemoveCache(methodEntry, where);
        }
    }
}
