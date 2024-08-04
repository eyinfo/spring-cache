package com.eyinfo.springcache.storage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.entity.BaseEntity;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.storage.entity.ModelCacheConditions;
import com.eyinfo.springcache.storage.entity.PageConditions;
import com.eyinfo.springcache.storage.entity.QueryConditions;
import com.eyinfo.springcache.storage.entity.SearchCondition;
import com.eyinfo.springcache.storage.events.OnCacheStrategy;
import com.eyinfo.springcache.strategy.DeleteStrategy;
import com.eyinfo.springcache.strategy.QueryListStrategy;
import com.eyinfo.springcache.strategy.QueryStrategy;
import com.github.pagehelper.PageInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/6/24
 * @Description:
 * @Modifier:
 * @ModifyContent:
 */
public class StorageManager {

    private static volatile StorageManager storageManager;
    private final QueryService queryService = new QueryService();
    private final InsertOrUpdateService insertOrUpdateService = new InsertOrUpdateService();
    private final DeleteService deleteService = new DeleteService();

    public static StorageManager getInstance() {
        if (storageManager == null) {
            synchronized (StorageManager.class) {
                if (storageManager == null) {
                    storageManager = new StorageManager();
                }
            }
        }
        return storageManager;
    }

    public <Dao, T extends BaseEntity> long insertOrUpdate(Dao dao, DbMethodEntry methodEntry, T entity, boolean skipCache) {
        return insertOrUpdateService.insertOrUpdate(dao, methodEntry, entity, skipCache);
    }

    public <Dao, T extends BaseEntity> long insertOrUpdate(Dao dao, DbMethodEntry methodEntry, T entity) {
        return this.insertOrUpdate(dao, methodEntry, entity, false);
    }

    public <R, Dao> PageInfo<R> queryPage(Dao dao, Class<R> itemClass, DbMethodEntry methodEntry, PageConditions conditions, boolean skipCache) {
        if (skipCache) {
            return queryService.select(dao, itemClass, methodEntry, conditions, null);
        }
        return queryService.select(dao, itemClass, methodEntry, conditions, new OnCacheStrategy<SearchCondition, PageInfo<R>, R>() {
            @Override
            public PageInfo<R> onQueryCache(DbMethodEntry methodEntry, SearchCondition conditions, Class<R> itemClass) {
                QueryListStrategy strategy = new QueryListStrategy();
                return strategy.query(methodEntry, conditions, itemClass, false);
            }

            @Override
            public void onDataCache(DbMethodEntry methodEntry, SearchCondition conditions, Object data) {
                QueryListStrategy strategy = new QueryListStrategy();
                strategy.save(methodEntry, conditions, data);
            }
        });
    }

    public <R, Dao> PageInfo<R> queryPage(Dao dao, Class itemClass, DbMethodEntry methodEntry, PageConditions conditions) {
        return queryPage(dao, itemClass, methodEntry, conditions, false);
    }

    public <R, Dao> List<R> queryList(Dao dao, Class<R> itemClass, DbMethodEntry methodEntry, QueryConditions conditions) {
        return queryService.select(dao, itemClass, methodEntry, conditions, new OnCacheStrategy<String, List<R>, R>() {
            @Override
            public List<R> onQueryCache(DbMethodEntry methodEntry, String conditions, Class<R> itemClass) {
                QueryListStrategy strategy = new QueryListStrategy();
                return strategy.query(methodEntry, conditions, itemClass, true);
            }

            @Override
            public void onDataCache(DbMethodEntry methodEntry, String conditions, Object data) {
                QueryListStrategy strategy = new QueryListStrategy();
                strategy.save(methodEntry, conditions, data);
            }
        });
    }

    public <R, Dao> List<R> queryList(Dao dao, Class<R> itemClass, DbMethodEntry methodEntry, List<String> listParams) {
        return queryService.select(dao, itemClass, methodEntry, listParams, new OnCacheStrategy<String, List<R>, R>() {
            @Override
            public List<R> onQueryCache(DbMethodEntry methodEntry, String conditions, Class<R> itemClass) {
                QueryListStrategy strategy = new QueryListStrategy();
                return strategy.query(methodEntry, conditions, itemClass, true);
            }

            @Override
            public void onDataCache(DbMethodEntry methodEntry, String conditions, Object data) {
                QueryListStrategy strategy = new QueryListStrategy();
                strategy.save(methodEntry, conditions, data);
            }
        });
    }

    public <R, Dao> List<R> queryList(Dao dao, Class<R> itemClass, DbMethodEntry methodEntry, HashMap<String, Object> mapParams) {
        return queryService.select(dao, itemClass, methodEntry, mapParams, new OnCacheStrategy<String, List<R>, R>() {
            @Override
            public List<R> onQueryCache(DbMethodEntry methodEntry, String conditions, Class<R> itemClass) {
                QueryListStrategy strategy = new QueryListStrategy();
                return strategy.query(methodEntry, conditions, itemClass, true);
            }

            @Override
            public void onDataCache(DbMethodEntry methodEntry, String conditions, Object data) {
                QueryListStrategy strategy = new QueryListStrategy();
                strategy.save(methodEntry, conditions, data);
            }
        });
    }

    public <R, Dao> List<R> queryListPlus(Dao dao, Class<R> itemClass, DbMethodEntry methodEntry, QueryWrapper queryWrapper) {
        return queryService.selectPlus(dao, itemClass, methodEntry, queryWrapper, new OnCacheStrategy<QueryWrapper, List<R>, R>() {
            @Override
            public List<R> onQueryCache(DbMethodEntry methodEntry, QueryWrapper queryWrapper, Class<R> targetClass) {
                QueryListStrategy strategy = new QueryListStrategy();
                return strategy.queryPlus(methodEntry, queryWrapper, targetClass, true);
            }

            @Override
            public void onDataCache(DbMethodEntry methodEntry, QueryWrapper queryWrapper, Object data) {
                QueryListStrategy strategy = new QueryListStrategy();
                strategy.savePlus(methodEntry, queryWrapper, data);
            }
        });
    }

    public <R extends BaseEntity, Dao> R queryModel(Dao dao, DbMethodEntry methodEntry, Class<R> entityClass, String where, boolean skipCache, boolean isMergeQuery) {
        if (dao == null || methodEntry == null || TextUtils.isEmpty(where)) {
            return null;
        }
        ModelCacheConditions conditions = new ModelCacheConditions();
        conditions.setWhere(where);
        conditions.setMergeQuery(isMergeQuery);
        return queryService.select(dao, entityClass, methodEntry, conditions, new OnCacheStrategy<ModelCacheConditions, R, R>() {
            @Override
            public R onQueryCache(DbMethodEntry methodEntry, ModelCacheConditions conditions, Class<R> entityClass) {
                QueryStrategy strategy = new QueryStrategy();
                return strategy.query(methodEntry, conditions.getWhere(), conditions.isMergeQuery(), entityClass, false);
            }

            @Override
            public void onDataCache(DbMethodEntry methodEntry, ModelCacheConditions conditions, Object data) {
                QueryStrategy strategy = new QueryStrategy();
                strategy.save(methodEntry, conditions.getWhere(), data, conditions.isMergeQuery());
            }
        }, skipCache);
    }

    public <R extends BaseEntity, Dao> R queryModelPlus(Dao dao, DbMethodEntry methodEntry, Class<R> entityClass, QueryWrapper queryWrapper, boolean skipCache, boolean isMergeQuery) {
        if (dao == null || methodEntry == null || queryWrapper == null) {
            return null;
        }
        ModelCacheConditions conditions = new ModelCacheConditions();
        conditions.setQueryWrapper(queryWrapper);
        conditions.setMergeQuery(isMergeQuery);
        return queryService.select(dao, entityClass, methodEntry, conditions, new OnCacheStrategy<ModelCacheConditions, R, R>() {
            @Override
            public R onQueryCache(DbMethodEntry methodEntry, ModelCacheConditions conditions, Class<R> targetClass) {
                QueryStrategy strategy = new QueryStrategy();
                return strategy.queryPlus(methodEntry, conditions.getQueryWrapper(), conditions.isMergeQuery(), targetClass, false);
            }

            @Override
            public void onDataCache(DbMethodEntry methodEntry, ModelCacheConditions conditions, Object data) {
                QueryStrategy strategy = new QueryStrategy();
                strategy.savePlus(methodEntry, conditions.getQueryWrapper(), data, conditions.isMergeQuery());
            }
        }, skipCache);
    }

    public <Dao> String queryModel(Dao dao, DbMethodEntry methodEntry, String where, boolean skipCache, boolean isMergeQuery) {
        if (dao == null || methodEntry == null || TextUtils.isEmpty(where)) {
            return null;
        }
        ModelCacheConditions conditions = new ModelCacheConditions();
        conditions.setWhere(where);
        conditions.setMergeQuery(isMergeQuery);
        return queryService.select(dao, methodEntry, conditions, new OnCacheStrategy<ModelCacheConditions, String, String>() {
            @Override
            public String onQueryCache(DbMethodEntry methodEntry, ModelCacheConditions conditions, Class<String> targetClass) {
                QueryStrategy strategy = new QueryStrategy();
                return strategy.query(methodEntry, conditions.getWhere(), conditions.isMergeQuery(), targetClass, false);
            }

            @Override
            public void onDataCache(DbMethodEntry methodEntry, ModelCacheConditions conditions, Object data) {
                QueryStrategy strategy = new QueryStrategy();
                strategy.save(methodEntry, conditions.getWhere(), data, conditions.isMergeQuery());
            }
        }, skipCache);
    }

    public <Dao, T> String queryModelPlus(Dao dao, DbMethodEntry methodEntry, QueryWrapper<T> queryWrapper, boolean skipCache, boolean isMergeQuery) {
        if (dao == null || methodEntry == null || queryWrapper == null) {
            return null;
        }
        ModelCacheConditions conditions = new ModelCacheConditions();
        conditions.setQueryWrapper(queryWrapper);
        conditions.setMergeQuery(isMergeQuery);
        return queryService.select(dao, methodEntry, conditions, new OnCacheStrategy<ModelCacheConditions, String, String>() {
            @Override
            public String onQueryCache(DbMethodEntry methodEntry, ModelCacheConditions conditions, Class<String> targetClass) {
                QueryStrategy strategy = new QueryStrategy();
                return strategy.queryPlus(methodEntry, conditions.getQueryWrapper(), conditions.isMergeQuery(), targetClass, false);
            }

            @Override
            public void onDataCache(DbMethodEntry methodEntry, ModelCacheConditions conditions, Object data) {
                QueryStrategy strategy = new QueryStrategy();
                strategy.savePlus(methodEntry, conditions.getQueryWrapper(), data, conditions.isMergeQuery());
            }
        }, skipCache);
    }

    public <Dao> String queryModel(Dao dao, DbMethodEntry methodEntry, String where, boolean isMergeQuery) {
        return queryModel(dao, methodEntry, where, false, isMergeQuery);
    }

    public <Dao, T> String queryModelPlus(Dao dao, DbMethodEntry methodEntry, QueryWrapper<T> queryWrapper, boolean isMergeQuery) {
        return queryModelPlus(dao, methodEntry, queryWrapper, false, isMergeQuery);
    }

    public <Dao> String queryModel(Dao dao, DbMethodEntry methodEntry, String where) {
        return queryModel(dao, methodEntry, where, true);
    }

    public <Dao, T> String queryModelPlus(Dao dao, DbMethodEntry methodEntry, QueryWrapper<T> queryWrapper) {
        return queryModelPlus(dao, methodEntry, queryWrapper, true);
    }

    public <R extends BaseEntity, Dao> R queryModel(Dao dao, DbMethodEntry methodEntry, Class<R> entityClass, String where, boolean isMergeQuery) {
        return queryModel(dao, methodEntry, entityClass, where, false, isMergeQuery);
    }

    public <R extends BaseEntity, Dao> R queryModelPlus(Dao dao, DbMethodEntry methodEntry, Class<R> entityClass, QueryWrapper queryWrapper, boolean isMergeQuery) {
        return queryModelPlus(dao, methodEntry, entityClass, queryWrapper, false, isMergeQuery);
    }

    public <R extends BaseEntity, Dao> R queryModel(Dao dao, DbMethodEntry methodEntry, Class<R> entityClass, String where) {
        return queryModel(dao, methodEntry, entityClass, where, true);
    }

    public <R extends BaseEntity, Dao, T> R queryModelPlus(Dao dao, DbMethodEntry methodEntry, Class<R> entityClass, QueryWrapper<T> queryWrapper) {
        return queryModelPlus(dao, methodEntry, entityClass, queryWrapper, true);
    }

    public <R extends BaseEntity, Dao> R queryModelById(Dao dao, DbMethodEntry methodEntry, Class<R> entityClass, String idValue, boolean skipCache, boolean isMergeQuery) {
        if (dao == null || methodEntry == null || entityClass == null || TextUtils.isEmpty(idValue)) {
            return null;
        }
        String where = String.format(" where `id`='%s'", idValue);
        return queryModel(dao, methodEntry, entityClass, where, isMergeQuery);
    }

    public <R extends BaseEntity, Dao> R queryModelById(Dao dao, DbMethodEntry methodEntry, Class<R> entityClass, String idValue, boolean isMergeQuery) {
        return queryModelById(dao, methodEntry, entityClass, idValue, false, isMergeQuery);
    }

    public <R extends BaseEntity, Dao> R queryModelById(Dao dao, DbMethodEntry methodEntry, Class<R> entityClass, String idValue) {
        return queryModelById(dao, methodEntry, entityClass, idValue, true);
    }

    public <Dao> void delete(Dao dao, DbMethodEntry methodEntry, String where) {
        if (dao == null || methodEntry == null || TextUtils.isEmpty(where)) {
            return;
        }
        deleteService.delete(dao, methodEntry, where, (methodEntry1, where1) -> {
            DeleteStrategy strategy = new DeleteStrategy();
            strategy.delete(methodEntry1, where1);
        });
    }

    public <Dao, T> void deletePlus(Dao dao, DbMethodEntry methodEntry, QueryWrapper<T> queryWrapper) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (dao == null || methodEntry == null || queryWrapper == null) {
            return;
        }
        deleteService.deletePlus(dao, methodEntry, queryWrapper, (methodEntry1, queryWrapper1) -> {
            DeleteStrategy strategy = new DeleteStrategy();
            strategy.deletePlus(methodEntry1, queryWrapper1);
        });
    }

    public <Dao> void delete(Dao dao, DbMethodEntry methodEntry, Class<?> entityClass, String idValue) {
        if (dao == null || methodEntry == null || entityClass == null || TextUtils.isEmpty(idValue)) {
            return;
        }
        String where = String.format(" `id`='%s'", idValue);
        delete(dao, methodEntry, where);
    }

    //清除缓存
    public void removeCache(DbMethodEntry methodEntry, String where) {
        if (methodEntry == null || TextUtils.isEmpty(where)) {
            return;
        }
        queryService.removeCache(methodEntry, where, new OnCacheStrategy<String, Void, Void>() {
            @Override
            public void onRemoveCache(DbMethodEntry methodEntry, String where) {
                DeleteStrategy strategy = new DeleteStrategy();
                strategy.delete(methodEntry, where);
            }
        });
    }

    public void cleanContainsPrefixCache(DbMethodEntry... entries) {
        if (entries != null) {
            for (DbMethodEntry entry : entries) {
                QueryListStrategy strategy = new QueryListStrategy();
                strategy.cleanContainsPrefixCache(entry);
            }
        }
    }
}
