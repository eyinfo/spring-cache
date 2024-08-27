package com.eyinfo.springcache.storage.mybatis;

public interface RawItemMapper<T> extends PrototypeMapper<T> {
    /**
     * 根据主键删除数据
     *
     * @param id 主键id
     * @return 大于0即为操作成功
     */
    int deleteByPrimaryKey(String id);

    /**
     * 插入数据
     *
     * @param entity 实体对象
     * @return 大于0即为操作成功
     */
    int insert(T entity);

    /**
     * 选择性插入数据
     *
     * @param entity 数据实体
     * @return 大于0表示操作成功
     */
    int insertSelective(T entity);

    /**
     * 根据主键id查询数据
     *
     * @param id 主键id
     * @return 数据bean
     */
    T selectByPrimaryKey(String id);

    /**
     * 选择性更新数据
     *
     * @param entity 数据实体
     * @return 大于0表示操作成功
     */
    int updateByPrimaryKeySelective(T entity);

    /**
     * 根据主键更新数据
     *
     * @param entity 数据实体
     * @return 大于0表示操作成功
     */
    int updateByPrimaryKey(T entity);
}
