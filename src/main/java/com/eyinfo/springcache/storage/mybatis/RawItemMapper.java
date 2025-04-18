package com.eyinfo.springcache.storage.mybatis;

public interface RawItemMapper<T> extends PrototypeMapper<T> {
    /**
     * 根据主键删除数据
     *
     * @param id 主键id
     * @return 大于0即为操作成功
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 选择性插入数据
     * 自增数据插入之后id会自动填充到entity中
     *
     * @param entity 数据实体
     * @return 大于0表示操作成功
     */
    int insertSelective(T entity);

    /**
     * 选择性更新数据
     *
     * @param entity 数据实体
     * @return 大于0表示操作成功
     */
    int updateByPrimaryKeySelective(T entity);
}
