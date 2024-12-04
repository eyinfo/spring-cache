package com.eyinfo.springcache.storage.mybatis;

import com.eyinfo.foundation.entity.BaseEntity;
import jakarta.annotation.Resource;

public class BaseService<T extends BaseEntity, M extends ItemMapper<T>> {

    /**
     * mybatis mapper
     */
    @Resource
    protected M itemMapper;
}
