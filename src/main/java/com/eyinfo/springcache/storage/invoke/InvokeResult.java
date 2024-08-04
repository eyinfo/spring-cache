package com.eyinfo.springcache.storage.invoke;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020-03-24
 * Description:反射结果
 * Modifier:
 * ModifyContent:
 */
public class InvokeResult {

    //是否处理成功
    private boolean success;
    //invoke result
    private Object result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
