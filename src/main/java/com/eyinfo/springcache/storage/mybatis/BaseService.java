package com.eyinfo.springcache.storage.mybatis;

import com.eyinfo.foundation.entity.BaseEntity;

public class BaseService<T extends BaseEntity, M extends ItemMapper<T>> {

    /**
     * mybatis mapper
     */
    protected M itemMapper;
}
