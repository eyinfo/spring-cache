package com.eyinfo.springcache.manager;

import com.eyinfo.foundation.encrypts.MD5Encrypt;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.entity.KeywordEntity;
import org.springframework.data.mongodb.core.MongoTemplate;

public class KeywordManager {

    String collectionName = "tbl_keywords";

    /// 查找关键字信息
    public KeywordEntity findKeywordInfo(MongoTemplate mongoTemplate, String keyword) {
        String id = MD5Encrypt.md5(keyword);
        KeywordEntity keywords = mongoTemplate.findById(id, KeywordEntity.class, collectionName);
        return keywords == null ? new KeywordEntity() : keywords;
    }

    public void saveKeywordInfo(MongoTemplate mongoTemplate, KeywordEntity keywordItem) {
        if (keywordItem == null || TextUtils.isEmpty(keywordItem.getId()) || TextUtils.isEmpty(keywordItem.getKeyword())) {
            return;
        }
        mongoTemplate.save(keywordItem, collectionName);
    }

    /// 保存关键字信息
    /// [isOpenFinish] true-表示通过三方api搜索时没有更多数据返回
    public void saveKeywordInfo(MongoTemplate mongoTemplate, String keyword, boolean isOpenFinish) {
        String id = MD5Encrypt.md5(keyword);
        KeywordEntity keywords = mongoTemplate.findById(id, KeywordEntity.class, collectionName);
        if (keywords == null || TextUtils.isEmpty(keywords.getId())) {
            return;
        }
        KeywordEntity keywordItem = new KeywordEntity();
        keywordItem.setId(id);
        keywordItem.setKeyword(keyword);
        keywordItem.setOpenFinish(isOpenFinish);
        saveKeywordInfo(mongoTemplate, keywordItem);
    }
}
