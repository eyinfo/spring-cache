package com.eyinfo.springcache.storage;

import com.eyinfo.foundation.Butterfly;
import com.eyinfo.foundation.entity.BaseEntity;
import com.eyinfo.foundation.utils.ConvertUtils;
import com.eyinfo.foundation.utils.GlobalUtils;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.strategy.QueryStrategy;

import java.lang.reflect.Field;
import java.math.BigDecimal;

class InsertOrUpdateService extends BaseService {

    /**
     * 插入或更新数据(数据id>0更新,<=0新增)
     * 单条数据缓存8小时
     *
     * @param entity 当前数据实体
     * @param <T>    数据类型
     * @return 主键值(空 - 表示新增或更新失败)
     */
    public <Dao, T extends BaseEntity> long insertOrUpdate(Dao dao, DbMethodEntry methodEntry, T entity, boolean skipCache) {
        if (dao == null || entity == null || methodEntry == null) {
            return 0;
        }
        if (TextUtils.isEmpty(methodEntry.getMethodName())) {
            methodEntry.setMethodName("insertOrUpdate");
        }
        Class<?> entityClass = entity.getClass();
        IdProperties idProperties = getLongIdValue(entity, entityClass);
        IdObject idObject = bindIdObject(idProperties);
        if (!insertOrUpdateFromDB(dao, methodEntry, entity, idObject)) {
            return 0;
        }
        if (!skipCache) {
            QueryStrategy strategy = new QueryStrategy();
            String where = String.format(" `id`='%s'", idObject.id);
            strategy.save(methodEntry, where, entity, true);
        }
        return idObject.longId;
    }

    private <T> T mergeModel(T entity, T source) {
        Class<?> sourceClass = source.getClass();
        Field[] fields = sourceClass.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            Object value = GlobalUtils.getPropertiesValue(entity, name);
            if (checkAssignment(value)) {
                Object sourceValue = GlobalUtils.getPropertiesValue(source, name);
                GlobalUtils.setPropertiesValue(entity, name, sourceValue);
            }
        }
        return entity;
    }

    //赋值检测
    private boolean checkAssignment(Object value) {
        if (((value instanceof String) && TextUtils.isEmpty(ConvertUtils.toString(value))) ||
                ((value instanceof Boolean) && (boolean) value == false) ||
                ((value instanceof Integer) && (int) value == 0) ||
                ((value instanceof Long) && (long) value == 0) ||
                ((value instanceof Float) && (float) value == 0f) ||
                ((value instanceof Double) && (double) value == 0) ||
                ((value instanceof BigDecimal) && (BigDecimal) value == BigDecimal.ZERO)) {
            return true;
        }
        return false;
    }

    private class IdObject {
        private String id;
        private long longId;
        private boolean isUpdate;
    }

    private IdObject bindIdObject(IdProperties idProperties) {
        IdObject idObject = new IdObject();
        if (idProperties.isLongKey) {
            if (idProperties.longValue == 0) {
                idObject.longId = Butterfly.getInstance().nextId();
                return idObject;
            }
            idObject.longId = idProperties.longValue;
            idObject.isUpdate = true;
            return idObject;
        } else {
            if (idProperties.value == null || idProperties.value.isEmpty()) {
                idObject.id = Butterfly.getInstance().nextIdWith();
                return idObject;
            }
            idObject.id = idProperties.value;
            idObject.isUpdate = true;
            return idObject;
        }
    }

    private class IdProperties {
        private String value;
        private long longValue;
        private boolean isLongKey = false;
    }

    private <T extends BaseEntity> IdProperties getIdValue(T entity, Class<?> entityClass) {
        IdProperties properties = new IdProperties();
        properties.isLongKey = false;
        properties.value = ConvertUtils.toString(GlobalUtils.getPropertiesValue(entity, "id"));
        if (!TextUtils.isEmpty(properties.value)) {
            return properties;
        }
        if (entityClass == BaseEntity.class) {
            return properties;
        }
        return getIdValue(entity, entityClass.getSuperclass());
    }

    private <T extends BaseEntity> IdProperties getLongIdValue(T entity, Class<?> entityClass) {
        IdProperties properties = new IdProperties();
        properties.isLongKey = true;
        properties.longValue = ConvertUtils.toLong(GlobalUtils.getPropertiesValue(entity, "id"));
        if (properties.longValue > 0) {
            return properties;
        }
        if (entityClass == BaseEntity.class) {
            return properties;
        }
        return getLongIdValue(entity, entityClass.getSuperclass());
    }

    private <Dao, T extends BaseEntity> boolean insertOrUpdateFromDB(Dao dao, DbMethodEntry methodEntry, T entity, IdObject idObject) {
        if (!idObject.isUpdate) {
            GlobalUtils.setPropertiesValue(entity, "id", idObject.longId);
        }
        super.invoke(dao, methodEntry, entity);
        return true;
    }
}
