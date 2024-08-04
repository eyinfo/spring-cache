package com.eyinfo.springcache.storage.invoke;

import com.eyinfo.foundation.utils.TextUtils;

import java.lang.reflect.Method;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/6/24
 * @Description:
 * @Modifier:
 * @ModifyContent:
 */
public class ServerInvoke {

    public Method getCurrentMethod(Class<?> serviceClass, String methodName) {
        if (methodName == null) {
            return null;
        }
        String className = serviceClass.getName();
        Method[] declaredMethods = serviceClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (TextUtils.equals(method.getName(), methodName)) {
                return method;
            }
        }
        return null;
    }

    public String getMatchMethodName(String className, String referenceMethodName) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        boolean startFlag = false;
        for (StackTraceElement element : stackTrace) {
            if (!startFlag && !TextUtils.equals(element.getMethodName(), referenceMethodName)) {
                continue;
            }
            startFlag = true;
            if (TextUtils.equals(element.getMethodName(), referenceMethodName)) {
                continue;
            }
            if (TextUtils.equals(className, element.getClassName())) {
                return element.getMethodName();
            }
        }
        return "";
    }
}
