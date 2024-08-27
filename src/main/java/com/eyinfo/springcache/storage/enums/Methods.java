package com.eyinfo.springcache.storage.enums;

/**
 * DbMethodEntry#methodName
 */
public enum Methods {
    /**
     * 根据主键删除
     */
    deleteByPrimaryKey,
    /**
     * 插入数据
     */
    insert,
    /**
     * 插入符合条件数据
     */
    insertSelective,
    /**
     * 根据主键查询数据
     */
    selectByPrimaryKey,
    /**
     * 根据实体更新符合条件数据(主键id更新条件)
     */
    updateByPrimaryKeySelective,
    /**
     * 根据实体更新数据(主键id更新条件)
     */
    updateByPrimaryKey,
    /**
     * 获取单条数据实体
     */
    getDataPlus,
    /**
     * 获取数据列表
     */
    getListPlus,
    /**
     * 统计数据
     */
    countPlus,
    /**
     * 删除数据
     */
    deletePlus,
    /**
     * 批量更新
     */
    updateItems,
    /**
     * 批量插入
     */
    insertItems,
    /**
     * 根据条件选择性更新（字段属性为空不作变更）
     */
    updateBySelective
}
