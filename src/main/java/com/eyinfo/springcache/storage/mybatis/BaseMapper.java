package com.eyinfo.springcache.storage.mybatis;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseMapper<T> {

    /**
     * 选择性插入数据
     *
     * @param entity 数据实体
     * @return 大于表示插入数据成功
     */
    int insertSelective(T entity);

    /**
     * 选择性更新数据
     *
     * @param entity 数据实体
     * @return 大于表示更新数据成功
     */
    int updateByPrimaryKeySelective(T entity);

    /**
     * 获取单条数据实体
     *
     * @param queryWrapper 查询条件
     * @return 数据对象
     */
    T getDataPlus(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 获取数据列表
     *
     * @param queryWrapper 查询条件
     * @return 数据集合
     */
    List<T> getListPlus(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 统计数据
     *
     * @param queryWrapper 查询条件
     * @return count
     */
    int countPlus(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 删除数据
     *
     * @param queryWrapper 查询条件
     */
    void deletePlus(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 批量更新
     *
     * @param items 待更新数据
     */
    void updateItems(@Param("items") List<T> items);

    /**
     * 批量插入数据
     *
     * @param items 待插入数据
     */
    void insertItems(@Param("items") List<T> items);

    /**
     * 根据条件选择性更新（字段属性为空不作变更）
     *
     * @param entity       更新对象
     * @param queryWrapper 查询条件
     * @return 大于0表示更新成功
     */
    int updateBySelective(@Param(Constants.ENTITY) T entity, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);
}
