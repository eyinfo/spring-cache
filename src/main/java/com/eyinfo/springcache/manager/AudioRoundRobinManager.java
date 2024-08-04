package com.eyinfo.springcache.manager;

import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.entity.AudioRoundRobinEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class AudioRoundRobinManager {

    String collectionName = "tbl_audio_generate_content";

    //随机查询一条记录
    public AudioRoundRobinEntity queryOne(MongoTemplate mongoTemplate) {
        Criteria criteria = new Criteria();
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sample(1));
        AggregationResults<AudioRoundRobinEntity> aggregate = mongoTemplate.aggregate(aggregation, collectionName, AudioRoundRobinEntity.class);
        AudioRoundRobinEntity robinItem = aggregate.getUniqueMappedResult();
        if (robinItem == null || TextUtils.isEmpty(robinItem.getId())) {
            return null;
        }
        return robinItem;
    }

    public List<AudioRoundRobinEntity> queryAllAndRemove(MongoTemplate mongoTemplate) {
        Query query = new Query();
        return mongoTemplate.findAllAndRemove(query, AudioRoundRobinEntity.class, collectionName);
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
    public void appendRoundRobinRecord(MongoTemplate mongoTemplate, AudioRoundRobinEntity roundRobinItem) {
        if (roundRobinItem == null || TextUtils.isEmpty(roundRobinItem.getId())) {
            return;
        }
        mongoTemplate.save(roundRobinItem, collectionName);
    }
}
