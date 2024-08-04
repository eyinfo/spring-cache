package com.eyinfo.springcache.storage.invoke;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020-03-21
 * Description:方法参数
 * Modifier:
 * ModifyContent:
 */
public class ArgumentsProperty {

    public ArgumentsProperty() {

    }

    public ArgumentsProperty(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    /**
     * 参数类型
     */
    private String type;

    //type与typeClass二选一
    private Class<?> typeClass;

    /**
     * 参数值
     */
    private Object value;

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
