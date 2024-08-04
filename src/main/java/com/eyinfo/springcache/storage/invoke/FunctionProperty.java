package com.eyinfo.springcache.storage.invoke;

import java.util.LinkedList;
import java.util.List;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020-03-19
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class FunctionProperty {

    public FunctionProperty() {

    }

    public FunctionProperty(int type, String name, List<ArgumentsProperty> args) {
        this.type = type;
        this.name = name;
        this.args = args;
    }

    /**
     * 1. static方法 2. instance方法
     * {@link InvokeType#ordinal()}
     */
    private int type;

    /**
     * 方法名
     */
    private String name;
    /**
     * 方法参数[]
     */
    private List<ArgumentsProperty> args;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ArgumentsProperty> getArgs() {
        return args == null ? args = new LinkedList<>() : args;
    }

    public void setArgs(List<ArgumentsProperty> args) {
        this.args = args;
    }
}
