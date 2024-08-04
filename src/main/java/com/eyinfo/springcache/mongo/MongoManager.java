package com.eyinfo.springcache.mongo;

import com.eyinfo.foundation.encrypts.MD5Encrypt;
import com.eyinfo.foundation.enums.Environment;
import com.eyinfo.foundation.utils.ObjectJudge;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.entity.MongoCacheEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.regex.Pattern;

public class MongoManager {

    private static MongoManager mongoManager;

    public static MongoManager getInstance() {
        if (mongoManager == null) {
            synchronized (MongoManager.class) {
                if (mongoManager == null) {
                    mongoManager = new MongoManager();
                }
            }
        }
        return mongoManager;
    }

    private String getCollectionName(Environment environment) {
        return String.format("tbl_cache_record_%s", environment.name());
    }

    /**
     * 数据缓存
     *
     * @param mongoTemplate mongo模板
     * @param environment   环境
     * @param key           缓存key
     * @param content       缓存数据
     * @param period        缓存时间段（毫秒）,与当前时间和缓存时时间差相比超过该缓存时间获取数据后自动删除，
     *                      小于等于0时永久性缓存。
     */
    public void set(MongoTemplate mongoTemplate, Environment environment, String key, String content, long period) {
        if (mongoTemplate == null || TextUtils.isEmpty(content)) {
            return;
        }
        MongoCacheEntity cacheItem = new MongoCacheEntity();
        cacheItem.setId(MD5Encrypt.md5(key));
        cacheItem.setKey(key);
        cacheItem.setContent(content);
        cacheItem.setCacheTime(System.currentTimeMillis());
        cacheItem.setPeriod(period);
        cacheItem.setPersistence(period <= 0);
        mongoTemplate.save(cacheItem, getCollectionName(environment));
    }

    /**
     * 数据缓存
     *
     * @param mongoTemplate mongo模板
     * @param environment   环境
     * @param key           缓存key
     * @param content       缓存数据
     */
    public void set(MongoTemplate mongoTemplate, Environment environment, String key, String content) {
        set(mongoTemplate, environment, key, content, 0);
    }

    /**
     * 根据id删除缓存
     *
     * @param mongoTemplate mongo模板
     * @param environment   环境
     * @param id            缓存记录id,缓存时由content md5等到
     */
    public void deleteById(MongoTemplate mongoTemplate, Environment environment, String id) {
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
     * @param mongoTemplate mongo模板
     * @param environment   环境
     * @param key           缓存key
     */
    public void deleteByKey(MongoTemplate mongoTemplate, Environment environment, String key) {
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
     * @param mongoTemplate mongo模板
     * @param environment   环境
     * @param keys          缓存key
     */
    public void blurDelete(MongoTemplate mongoTemplate, Environment environment, List<String> keys) {
        if (mongoTemplate == null || ObjectJudge.isNullOrEmpty(keys)) {
            return;
        }
        Query query = new Query();
        Criteria criteria = new Criteria();
        Criteria[] conditions = new Criteria[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            conditions[i] = Criteria.where("key").regex(Pattern.compile("^" + keys.get(i) + ".*$"));
        }
        criteria.orOperator(conditions);
        query.addCriteria(criteria);
        mongoTemplate.remove(query, getCollectionName(environment));
    }

    /**
     * 根据id获取缓存
     *
     * @param mongoTemplate mongo模板
     * @param environment   环境
     * @param id            缓存记录id,缓存时由content md5等到
     * @return 返回缓存数据
     */
    public String getById(MongoTemplate mongoTemplate, Environment environment, String id) {
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
     * @param mongoTemplate mongo模板
     * @param environment   环境
     * @param key           缓存记录key
     * @return 返回缓存数据
     */
    public String getByKey(MongoTemplate mongoTemplate, Environment environment, String key) {
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
}
