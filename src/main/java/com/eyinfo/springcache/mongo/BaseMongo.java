package com.eyinfo.springcache.mongo;

import com.eyinfo.foundation.encrypts.MD5Encrypt;
import com.eyinfo.foundation.enums.Environment;
import com.eyinfo.foundation.utils.ObjectJudge;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.entity.CachingStrategyConfig;
import com.eyinfo.springcache.entity.MongoCacheEntity;
import com.eyinfo.springcache.entity.MongoItem;
import com.eyinfo.springcache.storage.StorageConfiguration;
import com.eyinfo.springcache.storage.StorageUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

abstract class BaseMongo {
    protected abstract String getCollectionName(Environment environment);

    public MongoTemplate getMongoTemplate() {
        StorageConfiguration configuration = StorageUtils.getConfiguration();
        if (configuration == null) {
            return null;
        }
        return configuration.getMongoTemplate();
    }

    private String getCollectionName(Environment environment, String customCollectionName) {
        if (TextUtils.isEmpty(customCollectionName)) {
            return getCollectionName(environment);
        }
        return customCollectionName;
    }

    /**
     * 数据缓存
     *
     * @param environment    环境
     * @param key            缓存key
     * @param content        缓存数据
     * @param period         缓存时间段（毫秒）,与当前时间和缓存时时间差相比超过该缓存时间获取数据后自动删除，
     *                       如需要永久缓存输入小于0的数字。
     * @param collectionName 集合名称
     */
    void set(Environment environment, String key, String content, Long period, String collectionName) {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (mongoTemplate == null || TextUtils.isEmpty(content)) {
            return;
        }
        MongoCacheEntity cacheItem = new MongoCacheEntity();
        cacheItem.setId(MD5Encrypt.md5(key));
        cacheItem.setKey(key);
        cacheItem.setContent(content);
        cacheItem.setCacheTime(System.currentTimeMillis());
        if (period == null) {
            CachingStrategyConfig strategyConfig = StorageUtils.getCachingStrategyConfig();
            Long mongoGlobalCacheTime = strategyConfig.getMongoGlobalCacheTime();
            if (mongoGlobalCacheTime == null) {
                cacheItem.setPeriod(0);
                cacheItem.setPersistence(true);
                mongoTemplate.save(cacheItem, getCollectionName(environment, collectionName));
            } else {
                cacheItem.setPeriod(mongoGlobalCacheTime);
                cacheItem.setPersistence(mongoGlobalCacheTime <= 0);
                mongoTemplate.save(cacheItem, getCollectionName(environment, collectionName));
            }
        } else {
            cacheItem.setPeriod(period);
            cacheItem.setPersistence(period <= 0);
            mongoTemplate.save(cacheItem, getCollectionName(environment, collectionName));
        }
    }

    /**
     * 数据缓存
     *
     * @param environment 环境
     * @param key         缓存key
     * @param content     缓存数据
     * @param period      缓存时间段（毫秒）,与当前时间和缓存时时间差相比超过该缓存时间获取数据后自动删除，
     *                    如需要永久缓存输入小于0的数字。
     */
    void set(Environment environment, String key, String content, Long period) {
        this.set(environment, key, content, period, null);
    }

    /**
     * 数据缓存
     *
     * @param environment    环境
     * @param key            缓存key
     * @param content        缓存数据
     * @param collectionName 集合名称
     */
    void set(Environment environment, String key, String content, String collectionName) {
        set(environment, key, content, -1L, collectionName);
    }

    /**
     * 数据缓存
     *
     * @param environment    环境
     * @param key            缓存key
     * @param content        缓存数据
     * @param collectionName 集合名称
     */
    void set(Environment environment, String key, String content) {
        this.set(environment, key, content, -1L, null);
    }

    /**
     * 根据id删除缓存
     *
     * @param environment    环境
     * @param id             缓存记录id,缓存时由content md5等到
     * @param collectionName 集合名称
     */
    void deleteById(Environment environment, String id, String collectionName) {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (mongoTemplate == null || TextUtils.isEmpty(id)) {
            return;
        }
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("id").is(id);
        query.addCriteria(criteria);
        mongoTemplate.remove(query, getCollectionName(environment, collectionName));
    }

    /**
     * 根据id删除缓存
     *
     * @param environment 环境
     * @param id          缓存记录id,缓存时由content md5等到
     */
    void deleteById(Environment environment, String id) {
        this.deleteById(environment, id, null);
    }

    /**
     * 根据key删除缓存
     *
     * @param environment    环境
     * @param key            缓存key
     * @param collectionName 集合名称
     */
    void deleteByKey(Environment environment, String key, String collectionName) {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (mongoTemplate == null || TextUtils.isEmpty(key)) {
            return;
        }
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("key").is(key);
        query.addCriteria(criteria);
        mongoTemplate.remove(query, getCollectionName(environment, collectionName));
    }

    /**
     * 根据key删除缓存
     *
     * @param environment 环境
     * @param key         缓存key
     */
    void deleteByKey(Environment environment, String key) {
        this.deleteByKey(environment, key, null);
    }

    /**
     * 模糊删除
     *
     * @param environment    环境
     * @param keys           缓存key
     * @param collectionName 集合名称
     */
    void blurDelete(Environment environment, Collection<String> keys, String collectionName) {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (mongoTemplate == null || ObjectJudge.isNullOrEmpty(keys)) {
            return;
        }
        Query query = new Query();
        Criteria criteria = new Criteria();
        Criteria[] conditions = new Criteria[keys.size()];
        Iterator<String> iterator = keys.iterator();
        int pos = 0;
        while (iterator.hasNext()) {
            conditions[pos] = Criteria.where("key").regex(Pattern.compile("^" + iterator.next() + ".*$"));
            pos++;
        }
        criteria.orOperator(conditions);
        query.addCriteria(criteria);
        mongoTemplate.remove(query, getCollectionName(environment, collectionName));
    }

    /**
     * 模糊删除
     *
     * @param environment 环境
     * @param keys        缓存key
     */
    void blurDelete(Environment environment, Collection<String> keys) {
        this.blurDelete(environment, keys, null);
    }

    /**
     * 根据id获取缓存
     *
     * @param environment    环境
     * @param id             缓存记录id,缓存时由content md5等到
     * @param collectionName 集合名称
     * @return 返回缓存数据
     */
    String getById(Environment environment, String id, String collectionName) {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (mongoTemplate == null || TextUtils.isEmpty(id)) {
            return "";
        }
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("id").is(id);
        query.addCriteria(criteria);
        MongoCacheEntity entity = mongoTemplate.findById(id, MongoCacheEntity.class, getCollectionName(environment, collectionName));
        if (entity == null) {
            return "";
        }
        if (entity.isPersistence()) {
            return entity.getContent();
        }
        long cacheTime = entity.getCacheTime();
        long diff = System.currentTimeMillis() - cacheTime;
        if (diff <= entity.getPeriod()) {
            return entity.getContent();
        }
        mongoTemplate.remove(query, getCollectionName(environment, collectionName));
        return "";
    }

    /**
     * 根据id获取缓存
     *
     * @param environment 环境
     * @param id          缓存记录id,缓存时由content md5等到
     * @return 返回缓存数据
     */
    String getById(Environment environment, String id) {
        return this.getById(environment, id, null);
    }

    /**
     * 根据key获取缓存
     *
     * @param environment    环境
     * @param key            缓存记录key
     * @param collectionName 集合名称
     * @return 返回缓存数据
     */
    String getByKey(Environment environment, String key, String collectionName) {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (mongoTemplate == null || TextUtils.isEmpty(key)) {
            return "";
        }
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("key").is(key);
        query.addCriteria(criteria);
        MongoCacheEntity entity = mongoTemplate.findOne(query, MongoCacheEntity.class, getCollectionName(environment, collectionName));
        if (entity == null) {
            return "";
        }
        if (entity.isPersistence()) {
            return entity.getContent();
        }
        long cacheTime = entity.getCacheTime();
        long diff = System.currentTimeMillis() - cacheTime;
        if (diff <= entity.getPeriod()) {
            return entity.getContent();
        }
        mongoTemplate.remove(query, getCollectionName(environment, collectionName));
        return "";
    }

    /**
     * 根据key获取缓存
     *
     * @param environment 环境
     * @param key         缓存记录key
     * @return 返回缓存数据
     */
    String getByKey(Environment environment, String key) {
        return this.getByKey(environment, key, null);
    }

    /**
     * 数据缓存
     *
     * @param key            缓存key
     * @param content        缓存数据
     * @param period         缓存时间段（毫秒）,与当前时间和缓存时时间差相比超过该缓存时间获取数据后自动删除
     * @param collectionName 集合名称
     */
    public void set(String key, String content, Long period, String collectionName) {
        Environment environment = StorageUtils.getEnvironment();
        this.set(environment, key, content, period, collectionName);
    }

    /**
     * 数据缓存
     *
     * @param key     缓存key
     * @param content 缓存数据
     * @param period  缓存时间段（毫秒）,与当前时间和缓存时时间差相比超过该缓存时间获取数据后自动删除
     */
    public void set(String key, String content, Long period) {
        this.set(key, content, period, null);
    }

    /**
     * 数据缓存
     *
     * @param key            缓存key
     * @param content        缓存数据
     * @param collectionName
     */
    public void set(String key, String content, String collectionName) {
        Environment environment = StorageUtils.getEnvironment();
        this.set(environment, key, content, collectionName);
    }

    /**
     * 数据缓存
     *
     * @param key     缓存key
     * @param content 缓存数据
     */
    public void set(String key, String content) {
        this.set(key, content, -1L, null);
    }

    /**
     * 根据id删除缓存
     *
     * @param id             缓存记录id,缓存时由content md5等到
     * @param collectionName 集合名称
     */
    public void deleteById(String id, String collectionName) {
        Environment environment = StorageUtils.getEnvironment();
        this.deleteById(environment, id, collectionName);
    }

    /**
     * 根据id删除缓存
     *
     * @param id 缓存记录id,缓存时由content md5等到
     */
    public void deleteById(String id) {
        this.deleteById(id, null);
    }

    /**
     * 根据key删除缓存
     *
     * @param key            缓存key
     * @param collectionName 集合名称
     */
    public void deleteByKey(String key, String collectionName) {
        Environment environment = StorageUtils.getEnvironment();
        this.deleteByKey(environment, key, collectionName);
    }

    /**
     * 根据key删除缓存
     *
     * @param key 缓存key
     */
    public void deleteByKey(String key) {
        this.deleteByKey(key, null);
    }

    /**
     * 模糊删除
     *
     * @param keys           缓存key
     * @param collectionName 集合名称
     */
    public void blurDelete(Collection<String> keys, String collectionName) {
        Environment environment = StorageUtils.getEnvironment();
        this.blurDelete(environment, keys, collectionName);
    }

    /**
     * 模糊删除
     *
     * @param keys 缓存key
     */
    public void blurDelete(Collection<String> keys) {
        this.blurDelete(keys, null);
    }

    /**
     * 根据id获取缓存
     *
     * @param id             缓存记录id,缓存时由content md5等到
     * @param collectionName 集合名称
     * @return 返回缓存数据
     */
    public String getById(String id, String collectionName) {
        Environment environment = StorageUtils.getEnvironment();
        return this.getById(environment, id, collectionName);
    }

    /**
     * 根据id获取缓存
     *
     * @param id 缓存记录id,缓存时由content md5等到
     * @return 返回缓存数据
     */
    public String getById(String id) {
        return this.getById(id, null);
    }

    /**
     * 根据key获取缓存
     *
     * @param key            缓存记录key
     * @param collectionName 集合名称
     * @return 返回缓存数据
     */
    public String getByKey(String key, String collectionName) {
        Environment environment = StorageUtils.getEnvironment();
        return this.getByKey(environment, key, collectionName);
    }

    /**
     * 根据key获取缓存
     *
     * @param key 缓存记录key
     * @return 返回缓存数据
     */
    public String getByKey(String key) {
        return this.getByKey(key, null);
    }

    /**
     * 设置自定义集合数据
     *
     * @param environment    环境
     * @param data           数据对象
     * @param collectionName 集合名称
     * @param <T>            数据类型
     */
    public <T extends MongoItem> void set(Environment environment, T data, String collectionName) {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (mongoTemplate == null || data == null || TextUtils.isEmpty(collectionName)) {
            return;
        }
        String collection = String.format("%s_%s", collectionName, environment.name());
        mongoTemplate.save(data, getCollectionName(environment, collection));
    }

    /**
     * 设置自定义集合数据
     *
     * @param environment    环境
     * @param data           数据对象
     * @param collectionName 集合名称
     * @param <T>            数据类型
     */
    public <T extends MongoItem> void set(T data, String collectionName) {
        Environment environment = StorageUtils.getEnvironment();
        this.set(environment, data, collectionName);
    }

    /**
     * 根据条件查询数据
     *
     * @param environment    环境
     * @param query          查询条件
     * @param itemClass      单条数据class
     * @param collectionName 集合名称
     * @return 返回集合数据
     */
    public <Item> List<Item> getQuery(Environment environment, Query query, Class<Item> itemClass, String collectionName) {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (mongoTemplate == null) {
            return null;
        }
        return mongoTemplate.find(query, itemClass, getCollectionName(environment, collectionName));
    }

    /**
     * 根据条件查询数据
     *
     * @param environment    环境
     * @param query          查询条件
     * @param itemClass      单条数据class
     * @param collectionName 集合名称
     * @return 返回集合数据
     */
    public <Item> List<Item> getQuery(Query query, Class<Item> itemClass, String collectionName) {
        Environment environment = StorageUtils.getEnvironment();
        return this.getQuery(environment, query, itemClass, collectionName);
    }

    /**
     * 根据id获取数据
     *
     * @param environment    环境
     * @param id             数据
     * @param itemClass      单条数据class
     * @param collectionName 集合名称
     * @return 返回集合数据
     */
    public <Item> Item getQueryById(String id, Class<Item> itemClass, String collectionName) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("id").is(id);
        query.addCriteria(criteria);
        List<Item> list = this.getQuery(query, itemClass, collectionName);
        if (ObjectJudge.isNullOrEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 根据ids获取数据
     *
     * @param environment    环境
     * @param ids            ids
     * @param itemClass      单条数据class
     * @param collectionName 集合名称
     * @return 返回集合数据
     */
    public <Item> List<Item> getQueryByIds(Collection<String> ids, Class<Item> itemClass, String collectionName) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("id").in(ids);
        query.addCriteria(criteria);
        return this.getQuery(query, itemClass, collectionName);
    }

    /**
     * 根据key获取数据
     *
     * @param environment    环境
     * @param key            记录key
     * @param itemClass      单条数据class
     * @param collectionName 集合名称
     * @return 返回集合数据
     */
    public <Item> List<Item> getQueryByKey(String key, Class<Item> itemClass, String collectionName) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("key").is(key);
        query.addCriteria(criteria);
        return this.getQuery(query, itemClass, collectionName);
    }

    /**
     * 根据key获取数据
     *
     * @param environment    环境
     * @param key            记录key
     * @param itemClass      单条数据class
     * @param collectionName 集合名称
     * @return 返回数据
     */
    public <Item> Item getQueryOneByKey(String key, Class<Item> itemClass, String collectionName) {
        List<Item> list = this.getQueryByKey(key, itemClass, collectionName);
        return list.size() > 0 ? list.get(0) : null;
    }
}
