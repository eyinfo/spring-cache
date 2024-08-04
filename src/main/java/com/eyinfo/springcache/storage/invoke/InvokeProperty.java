package com.eyinfo.springcache.storage.invoke;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020-03-19
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class InvokeProperty {

    /**
     * 类名
     */
    private String name;

    /**
     * 方法列表
     */
    private FunctionProperty functions;

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FunctionProperty getFunctions() {
        return functions;
    }

    public void setFunctions(FunctionProperty functions) {
        this.functions = functions;
    }
}
