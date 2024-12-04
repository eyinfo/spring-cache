package com.eyinfo.springcache.storage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eyinfo.foundation.encrypts.MD5Encrypt;
import com.eyinfo.foundation.utils.ObjectJudge;
import com.eyinfo.springcache.storage.entity.ModelCacheConditions;
import com.eyinfo.springcache.storage.entity.PageConditions;
import com.eyinfo.springcache.storage.entity.SearchCondition;
import com.eyinfo.springcache.storage.events.OnCacheStrategy;
import com.eyinfo.springcache.storage.mybatis.PrototypeMapper;
import com.eyinfo.springcache.storage.strategy.DeleteStrategy;
import com.eyinfo.springcache.storage.strategy.QueryListStrategy;
import com.eyinfo.springcache.storage.strategy.QueryStrategy;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/6/24
 * @Description:
 * @Modifier:
 * @ModifyContent:
 */
public class StorageManager extends WithService {

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

    /**
     * 查询分页数据
     *
     * @param dao         table mapper
     * @param itemClass   data class
     * @param methodEntry methodEntry
     * @param conditions  查询条件
     * @param skipCache   true-查询之后缓存数据；false-直接返回数据；
     * @param <Item>      数据类型
     * @param <Dao>       mapper type
     * @return PageInfo分页数据
     */
    public <Item, Dao> PageInfo<Item> queryPage(Dao dao, Class<Item> itemClass, PageConditions conditions, boolean skipCache) {
        DbMethodEntry methodEntry = DbMethodManager.getList(MD5Encrypt.md5(String.valueOf(itemClass.hashCode())));
        if (skipCache) {
            return queryService.select(dao, itemClass, methodEntry, conditions, null);
        }
        return queryService.select(dao, itemClass, methodEntry, conditions, new OnCacheStrategy<>() {
            @Override
            public PageInfo<Item> onQueryCache(DbMethodEntry methodEntry, SearchCondition conditions, Class<Item> itemClass) {
                QueryListStrategy strategy = new QueryListStrategy();
                return strategy.query(methodEntry, conditions, PageInfo.class, false);
            }

            @Override
            public void onDataCache(DbMethodEntry methodEntry, SearchCondition conditions, Object data, Class<Item> itemClass) {
                QueryListStrategy strategy = new QueryListStrategy();
                strategy.save(methodEntry, conditions, data, itemClass);
            }
        });
    }

    /**
     * 查询分页数据
     *
     * @param dao         table mapper
     * @param itemClass   data class
     * @param methodEntry methodEntry
     * @param conditions  查询条件
     * @param <Item>      数据类型
     * @param <Dao>       mapper type
     * @return PageInfo分页数据
     */
    public <Item, Dao extends PrototypeMapper<Item>> PageInfo<Item> queryPage(Dao dao, Class itemClass, PageConditions conditions) {
        return queryPage(dao, itemClass, conditions, false);
    }

    /**
     * 查询数据列表
     *
     * @param dao          table mapper
     * @param itemClass    data class
     * @param methodEntry  methodEntry
     * @param queryWrapper 查询条件
     * @param <Item>       数据类型
     * @param <Dao>        mapper type
     * @return 数据列表
     */
    public <Item, Dao extends PrototypeMapper<Item>> List<Item> queryListPlus(Dao dao, Class<Item> itemClass, QueryWrapper queryWrapper) {
        DbMethodEntry methodEntry = DbMethodManager.getList(MD5Encrypt.md5(String.valueOf(itemClass.hashCode())));
        return queryService.selectPlus(dao, itemClass, methodEntry, queryWrapper, new OnCacheStrategy<>() {
            @Override
            public List<Item> onQueryCache(DbMethodEntry methodEntry, QueryWrapper queryWrapper, Class<Item> targetClass) {
                QueryListStrategy strategy = new QueryListStrategy();
                return strategy.queryPlus(methodEntry, queryWrapper, targetClass, true);
            }

            @Override
            public void onDataCache(DbMethodEntry methodEntry, QueryWrapper queryWrapper, Object data, Class<Item> targetClass) {
                QueryListStrategy strategy = new QueryListStrategy();
                strategy.savePlus(methodEntry, queryWrapper, data, targetClass);
            }
        });
    }

    /**
     * 查询数据
     *
     * @param dao          table mapper
     * @param methodEntry  methodEntry
     * @param entityClass  data class
     * @param queryWrapper 查询条件
     * @param skipCache    true-查询之后缓存数据；false-直接返回数据；
     * @param isMergeQuery true-在查询同一对象数据时，合并数据的缓存策略；false-不做合并处理；
     * @param <T>          数据类型
     * @param <Dao>        mapper type
     * @return
     */
    public <T, Dao extends PrototypeMapper<T>> T queryModelPlus(Dao dao, Class<T> entityClass, QueryWrapper queryWrapper, boolean skipCache, boolean isMergeQuery) {
        if (dao == null || queryWrapper == null) {
            return null;
        }
        DbMethodEntry methodEntry = DbMethodManager.getList(MD5Encrypt.md5(String.valueOf(entityClass.hashCode())));

        ModelCacheConditions conditions = new ModelCacheConditions();
        conditions.setQueryWrapper(queryWrapper);
        conditions.setMergeQuery(isMergeQuery);
        return queryService.select(dao, entityClass, methodEntry, conditions, new OnCacheStrategy<>() {
            @Override
            public T onQueryCache(DbMethodEntry methodEntry, ModelCacheConditions conditions, Class<T> targetClass) {
                QueryStrategy strategy = new QueryStrategy();
                return strategy.queryPlus(methodEntry, conditions.getQueryWrapper(), conditions.isMergeQuery(), targetClass, false);
            }

            @Override
            public void onDataCache(DbMethodEntry methodEntry, ModelCacheConditions conditions, Object data, Class<T> targetClass) {
                QueryStrategy strategy = new QueryStrategy();
                strategy.savePlus(methodEntry, conditions.getQueryWrapper(), data, conditions.isMergeQuery(), targetClass);
            }
        }, skipCache);
    }

    public <Dao> String queryModelPlus(Dao dao, QueryWrapper queryWrapper, boolean skipCache, boolean isMergeQuery) {
        if (dao == null || queryWrapper == null) {
            return null;
        }
        Class entityClass = queryWrapper.getEntityClass();
        DbMethodEntry methodEntry = DbMethodManager.getList(MD5Encrypt.md5(String.valueOf(entityClass.hashCode())));

        ModelCacheConditions conditions = new ModelCacheConditions();
        conditions.setQueryWrapper(queryWrapper);
        conditions.setMergeQuery(isMergeQuery);
        return queryService.select(dao, methodEntry, conditions, new OnCacheStrategy<>() {
            @Override
            public String onQueryCache(DbMethodEntry methodEntry, ModelCacheConditions conditions, Class<String> targetClass) {
                QueryStrategy strategy = new QueryStrategy();
                return strategy.queryPlus(methodEntry, conditions.getQueryWrapper(), conditions.isMergeQuery(), targetClass, false);
            }

            @Override
            public void onDataCache(DbMethodEntry methodEntry, ModelCacheConditions conditions, Object data, Class<String> targetClass) {
                QueryStrategy strategy = new QueryStrategy();
                strategy.savePlus(methodEntry, conditions.getQueryWrapper(), data, conditions.isMergeQuery(), targetClass);
            }
        }, skipCache);
    }

    public <Dao, T> String queryModelPlus(Dao dao, QueryWrapper<T> queryWrapper, boolean isMergeQuery) {
        return queryModelPlus(dao, queryWrapper, false, isMergeQuery);
    }

    public <Dao, T> String queryModelPlus(Dao dao, QueryWrapper<T> queryWrapper) {
        return queryModelPlus(dao, queryWrapper, true);
    }

    /**
     * 查询数据
     *
     * @param dao          table mapper
     * @param methodEntry  methodEntry
     * @param entityClass  data class
     * @param queryWrapper 查询条件
     * @param isMergeQuery true-在查询同一对象数据时，合并数据的缓存策略；false-不做合并处理；
     * @param <T>          数据类型
     * @param <Dao>        mapper type
     * @return
     */
    public <T, Dao extends PrototypeMapper<T>> T queryModelPlus(Dao dao, Class<T> entityClass, QueryWrapper queryWrapper, boolean isMergeQuery) {
        return queryModelPlus(dao, entityClass, queryWrapper, false, isMergeQuery);
    }

    /**
     * 查询数据
     *
     * @param dao          table mapper
     * @param methodEntry  methodEntry
     * @param entityClass  data class
     * @param queryWrapper 查询条件
     * @param <T>          数据类型
     * @param <Dao>        mapper type
     * @return
     */
    public <T, Dao extends PrototypeMapper<T>> T queryModelPlus(Dao dao, Class<T> entityClass, QueryWrapper queryWrapper) {
        return queryModelPlus(dao, entityClass, queryWrapper, true);
    }

    /**
     * 查询数据
     *
     * @param dao          table mapper
     * @param methodEntry  methodEntry
     * @param entityClass  data class
     * @param idValue      id数据
     * @param skipCache    true-查询之后缓存数据；false-直接返回数据；
     * @param isMergeQuery true-在查询同一对象数据时，合并数据的缓存策略；false-不做合并处理；
     * @param <T>          数据类型
     * @param <Dao>        mapper type
     * @return
     */
    public <T, Dao extends PrototypeMapper<T>> T queryModelById(Dao dao, Class<T> entityClass, Long idValue, boolean skipCache, boolean isMergeQuery) {
        if (entityClass == null || ObjectJudge.isNullOrZero(idValue)) {
            return null;
        }
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", idValue);
        return queryModelPlus(dao, entityClass, queryWrapper, skipCache, isMergeQuery);
    }

    /**
     * 查询数据
     *
     * @param dao          table mapper
     * @param methodEntry  methodEntry
     * @param entityClass  data class
     * @param idValue      id数据
     * @param isMergeQuery true-在查询同一对象数据时，合并数据的缓存策略；false-不做合并处理；
     * @param <T>          数据类型
     * @param <Dao>        mapper type
     * @return
     */
    public <T, Dao extends PrototypeMapper<T>> T queryModelById(Dao dao, Class<T> entityClass, Long idValue, boolean isMergeQuery) {
        return queryModelById(dao, entityClass, idValue, false, isMergeQuery);
    }

    /**
     * 查询数据
     *
     * @param dao         table mapper
     * @param methodEntry methodEntry
     * @param entityClass data class
     * @param idValue     id数据
     * @param <T>         数据类型
     * @param <Dao>       mapper type
     * @return
     */
    public <T, Dao extends PrototypeMapper<T>> T queryModelById(Dao dao, Class<T> entityClass, Long idValue) {
        return queryModelById(dao, entityClass, idValue, true);
    }

    public <Dao extends PrototypeMapper<?>, T> void deletePlus(Dao dao, Class<T> itemClass, QueryWrapper queryWrapper) {
        if (dao == null || queryWrapper == null) {
            return;
        }
        DbMethodEntry methodEntry = DbMethodManager.getList(MD5Encrypt.md5(String.valueOf(itemClass.hashCode())));

        deleteService.deletePlus(dao, methodEntry, queryWrapper, itemClass, (methodEntry1, queryWrapper1, targetClass) -> {
            DeleteStrategy strategy = new DeleteStrategy();
            strategy.deletePlus(methodEntry1, queryWrapper1, targetClass);
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
