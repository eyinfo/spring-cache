package com.eyinfo.springcache.manager;

import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.entity.HtmlGenerateDataEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class HtmlGenerateDataManager {

    String collectionName = "tbl_html_generate_by_content";

    //随机查询一条记录
    public HtmlGenerateDataEntity queryOne(MongoTemplate mongoTemplate) {
        HtmlGenerateDataEntity robinItem = mongoTemplate.findAndRemove(new Query(), HtmlGenerateDataEntity.class, collectionName);
        return robinItem;
    }

    public List<HtmlGenerateDataEntity> queryAllAndRemove(MongoTemplate mongoTemplate) {
        Query query = new Query();
        return mongoTemplate.findAllAndRemove(query, HtmlGenerateDataEntity.class, collectionName);
    }

    //删除记录
    public void deleteRecord(MongoTemplate mongoTemplate, String id) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        query.addCriteria(criteria);
        mongoTemplate.remove(query, collectionName);
    }

    //追加音频轮循记录
    public void appendRoundRobinRecord(MongoTemplate mongoTemplate, HtmlGenerateDataEntity roundRobinItem) {
        if (roundRobinItem == null || TextUtils.isEmpty(roundRobinItem.getId())) {
            return;
        }
        mongoTemplate.save(roundRobinItem, collectionName);
    }
}
