package com.eyinfo.springcache.mongo;

import com.eyinfo.foundation.encrypts.MD5Encrypt;
import com.eyinfo.foundation.enums.Environment;
import com.eyinfo.foundation.utils.ObjectJudge;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.entity.CachingStrategyConfig;
import com.eyinfo.springcache.entity.MongoCacheEntity;
import com.eyinfo.springcache.storage.StorageConfiguration;
import com.eyinfo.springcache.storage.StorageUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.Iterator;
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

    /**
     * 数据缓存
     *
     * @param environment 环境
     * @param key         缓存key
     * @param content     缓存数据
     * @param period      缓存时间段（毫秒）,与当前时间和缓存时时间差相比超过该缓存时间获取数据后自动删除，
     *                    小于等于0时永久性缓存。
     */
    void set(Environment environment, String key, String content, Long period) {
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
                mongoTemplate.save(cacheItem, getCollectionName(environment));
            } else {
                cacheItem.setPeriod(mongoGlobalCacheTime);
                cacheItem.setPersistence(mongoGlobalCacheTime <= 0);
                mongoTemplate.save(cacheItem, getCollectionName(environment));
            }
        } else {
            cacheItem.setPeriod(period);
            cacheItem.setPersistence(period <= 0);
            mongoTemplate.save(cacheItem, getCollectionName(environment));
        }
    }

    /**
     * 数据缓存
     *
     * @param environment 环境
     * @param key         缓存key
     * @param content     缓存数据
     */
    void set(Environment environment, String key, String content) {
        set(environment, key, content, null);
    }

    /**
     * 根据id删除缓存
     *
     * @param environment 环境
     * @param id          缓存记录id,缓存时由content md5等到
     */
    void deleteById(Environment environment, String id) {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (mongoTemplate == null || TextUtils.isEmpty(id)) {
            return;
        }
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("id").is(id);
        query.addCriteria(criteria);
        mongoTemplate.remove(query, getCollectionName(environment));
    }

    /**
     * 根据key删除缓存
     *
     * @param environment 环境
     * @param key         缓存key
     */
    void deleteByKey(Environment environment, String key) {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (mongoTemplate == null || TextUtils.isEmpty(key)) {
            return;
        }
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("key").is(key);
        query.addCriteria(criteria);
        mongoTemplate.remove(query, getCollectionName(environment));
    }

    /**
     * 模糊删除
     *
     * @param environment 环境
     * @param keys        缓存key
     */
    void blurDelete(Environment environment, Collection<String> keys) {
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
        mongoTemplate.remove(query, getCollectionName(environment));
    }

    /**
     * 根据id获取缓存
     *
     * @param environment 环境
     * @param id          缓存记录id,缓存时由content md5等到
     * @return 返回缓存数据
     */
    String getById(Environment environment, String id) {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (mongoTemplate == null || TextUtils.isEmpty(id)) {
            return "";
        }
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("id").is(id);
        query.addCriteria(criteria);
        MongoCacheEntity entity = mongoTemplate.findById(id, MongoCacheEntity.class, getCollectionName(environment));
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
        mongoTemplate.remove(query, getCollectionName(environment));
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
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (mongoTemplate == null || TextUtils.isEmpty(key)) {
            return "";
        }
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("key").is(key);
        query.addCriteria(criteria);
        MongoCacheEntity entity = mongoTemplate.findOne(query, MongoCacheEntity.class, getCollectionName(environment));
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
        mongoTemplate.remove(query, getCollectionName(environment));
        return "";
    }

    /**
     * 数据缓存
     *
     * @param key     缓存key
     * @param content 缓存数据
     * @param period  缓存时间段（毫秒）,与当前时间和缓存时时间差相比超过该缓存时间获取数据后自动删除，
     */
    public void set(String key, String content, Long period) {
        Environment environment = StorageUtils.getEnvironment();
        this.set(environment, key, content, period);
    }

    /**
     * 数据缓存
     *
     * @param key     缓存key
     * @param content 缓存数据
     */
    public void set(String key, String content) {
        Environment environment = StorageUtils.getEnvironment();
        this.set(environment, key, content);
    }

    /**
     * 根据id删除缓存
     *
     * @param id 缓存记录id,缓存时由content md5等到
     */
    public void deleteById(String id) {
        Environment environment = StorageUtils.getEnvironment();
        this.deleteById(environment, id);
    }

    /**
     * 根据key删除缓存
     *
     * @param key 缓存key
     */
    public void deleteByKey(String key) {
        Environment environment = StorageUtils.getEnvironment();
        this.deleteByKey(environment, key);
    }

    /**
     * 模糊删除
     *
     * @param keys 缓存key
     */
    public void blurDelete(Collection<String> keys) {
        Environment environment = StorageUtils.getEnvironment();
        this.blurDelete(environment, keys);
    }

    /**
     * 根据id获取缓存
     *
     * @param id 缓存记录id,缓存时由content md5等到
     * @return 返回缓存数据
     */
    public String getById(String id) {
        Environment environment = StorageUtils.getEnvironment();
        return this.getById(environment, id);
    }

    /**
     * 根据key获取缓存
     *
     * @param key 缓存记录key
     * @return 返回缓存数据
     */
    public String getByKey(String key) {
        Environment environment = StorageUtils.getEnvironment();
        return this.getByKey(environment, key);
    }
}
