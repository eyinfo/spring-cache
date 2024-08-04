package com.eyinfo.springcache.storage.invoke;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020-03-20
 * Description:
 * Modifier:
 * ModifyContent:
 */
class InvokeUtils {

    public ClassParsing getClassInstance(String instanceClassName) {
        ClassParsing classParsing = new ClassParsing();
        try {
            classParsing.instanceClass = Class.forName(instanceClassName);
            classParsing.instance = classParsing.instanceClass.newInstance();
            return classParsing;
        } catch (Exception e) {
            return classParsing;
        }
    }

    public ClassParsing getClassInstance(Class<?> instanceClass) {
        ClassParsing classParsing = new ClassParsing();
        try {
            classParsing.instanceClass = instanceClass;
            classParsing.instance = classParsing.instanceClass.newInstance();
            return classParsing;
        } catch (Exception e) {
            return classParsing;
        }
    }

    public ClassParsing getClassInstance(Object instance) {
        ClassParsing classParsing = new ClassParsing();
        classParsing.instanceClass = instance.getClass();
        classParsing.instance = instance;
        return classParsing;
    }
}
