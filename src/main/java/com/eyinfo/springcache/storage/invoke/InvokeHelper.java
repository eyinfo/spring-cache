package com.eyinfo.springcache.storage.invoke;

import com.eyinfo.foundation.CommonException;
import com.eyinfo.foundation.encrypts.MD5Encrypt;
import com.eyinfo.foundation.utils.JsonUtils;
import com.eyinfo.foundation.utils.ObjectJudge;
import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.foundation.utils.ValidUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2020-03-24
 * Description:反射工具类
 * Modifier:
 * ModifyContent:
 */
public class InvokeHelper {

    private InvokeUtils invokeUtils = new InvokeUtils();
    private static Map<String, Method> methodMap = new HashMap<>();

    private Method getMethod(FunctionProperty property, ClassParsing classParsing) throws NoSuchMethodException {
        Method method = null;
        if (ObjectJudge.isNullOrEmpty(property.getArgs())) {
            String methodKey = MD5Encrypt.md5(property.getName());
            Method cacheMethod = methodMap.get(methodKey);
            if (cacheMethod == null) {
                method = classParsing.instanceClass.getDeclaredMethod(property.getName());
                methodMap.put(methodKey, method);
            } else {
                method = cacheMethod;
            }
        } else {
            ParameterTypesEntry entry = getParameterTypes(property.getArgs());
            String methodKey = MD5Encrypt.md5(String.format("%s_%s", property.getName(), entry.parameterAlias));
            Method cacheMethod = methodMap.get(methodKey);
            if (cacheMethod == null) {
                method = classParsing.instanceClass.getDeclaredMethod(property.getName(), entry.parameterTypes);
                methodMap.put(methodKey, method);
            } else {
                method = cacheMethod;
            }
        }
        return method;
    }

    public InvokeResult invoke(InvokeProperty invokeProperty, ClassParsing classParsing) {
        try {
            InvokeResult invokeResult = new InvokeResult();
            if (classParsing == null || classParsing.instanceClass == null || classParsing.instance == null) {
                return invokeResult;
            }
            FunctionProperty functions = invokeProperty.getFunctions();
            if (functions == null) {
                return invokeResult;
            }
            Method method = getMethod(functions, classParsing);
            Object[] args = getArgs(functions.getArgs());
            int parameterCount = method.getParameterCount();
            method.setAccessible(true);
            if (parameterCount > 0) {
                invokeResult.setResult(method.invoke(classParsing.instance, args));
            } else {
                invokeResult.setResult(method.invoke(classParsing.instance));
            }
            invokeResult.setSuccess(true);
            return invokeResult;
        } catch (Throwable e) {
            throw new CommonException(e);
        }
    }

    public InvokeResult invoke(InvokeProperty invokeProperty, String instanceClassName) {
        ClassParsing classParsing = invokeUtils.getClassInstance(instanceClassName);
        return invoke(invokeProperty, classParsing);
    }

    public InvokeResult invoke(Object instance, InvokeProperty invokeProperty) {
        ClassParsing classParsing = invokeUtils.getClassInstance(instance);
        return invoke(invokeProperty, classParsing);
    }

    private ParameterTypesEntry getParameterTypes(List<ArgumentsProperty> args) {
        ParameterTypesEntry entry = new ParameterTypesEntry();
        Class<?>[] cls = new Class[ObjectJudge.isNullOrEmpty(args) ? 0 : args.size()];
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
            ArgumentsProperty arg = args.get(i);
            matchArgType(cls, i, arg);
            builder.append(arg.getTypeClass() == null ? arg.getType() : arg.getTypeClass().getSimpleName());
            builder.append(i);
        }
        entry.parameterTypes = cls;
        entry.parameterAlias = builder.toString();
        return entry;
    }

    private class ParameterTypesEntry {
        private Class<?>[] parameterTypes;
        private String parameterAlias;
    }

    private void matchArgType(Class<?>[] cls, int position, ArgumentsProperty property) {
        String typeName = property.getType().toLowerCase().trim();
        switch (typeName) {
            case "int":
                cls[position] = int.class;
                break;
            case "int[]":
                cls[position] = int[].class;
                break;
            case "float":
                cls[position] = float.class;
                break;
            case "float[]":
                cls[position] = float[].class;
                break;
            case "double":
                cls[position] = double.class;
                break;
            case "double[]":
                cls[position] = double[].class;
                break;
            case "long":
                cls[position] = long.class;
                break;
            case "long[]":
                cls[position] = long[].class;
                break;
            case "string":
                cls[position] = String.class;
                break;
            case "string[]":
                cls[position] = String[].class;
                break;
            case "list<string>":
            case "list<int>":
            case "list<float>":
            case "list<double>":
                cls[position] = List.class;
                break;
            case "iterable":
                cls[position] = Iterable.class;
                break;
            case "map<string, object>":
            case "map":
            case "hashmap<string, object>":
            case "hashmap":
                cls[position] = HashMap.class;
                break;
            case "object":
                cls[position] = Object.class;
                break;
            default:
                Class<?> typeClass = property.getTypeClass();
                cls[position] = typeClass;
                break;
        }
    }

    private Object[] getArgs(List<ArgumentsProperty> args) {
        Object[] obj = new Object[ObjectJudge.isNullOrEmpty(args) ? 0 : args.size()];
        for (int i = 0; i < args.size(); i++) {
            ArgumentsProperty arg = args.get(i);
            matchArgValue(obj, i, arg);
        }
        return obj;
    }

    private void matchArgValue(Object[] obj, int position, ArgumentsProperty arg) {
        String typeName = arg.getType().toLowerCase().trim();
        Object value = arg.getValue();
        switch (typeName) {
            case "int":
            case "float":
            case "double":
            case "long":
            case "string":
                obj[position] = arg.getValue();
                break;
            case "list<string>": {
                if (value == null || value instanceof List) {
                    obj[position] = value;
                } else {
                    List<String> list = getParseValue(value, String.class);
                    obj[position] = list == null ? null : list.toArray(new String[0]);
                }
                break;
            }
            case "list<int>": {
                if (value == null || value instanceof List) {
                    obj[position] = value;
                } else {
                    List<Integer> list = getParseValue(value, Integer.class);
                    obj[position] = list == null ? null : list.toArray(new Integer[0]);
                }
                break;
            }
            case "list<float>": {
                if (value == null || value instanceof List) {
                    obj[position] = value;
                } else {
                    List<Float> list = getParseValue(value, Float.class);
                    obj[position] = list == null ? null : list.toArray(new Float[0]);
                }
                break;
            }
            case "list<double>": {
                if (value == null || value instanceof List) {
                    obj[position] = value;
                } else {
                    List<Double> list = getParseValue(value, Double.class);
                    obj[position] = list == null ? null : list.toArray(new Double[0]);
                }
                break;
            }
            case "int[]": {
                if (value == null || value instanceof int[]) {
                    obj[position] = value;
                } else {
                    List<Integer> list = getParseValue(value, Integer.class);
                    obj[position] = list == null ? null : list.toArray(new Integer[0]);
                }
                break;
            }
            case "float[]": {
                if (value == null || value instanceof float[]) {
                    obj[position] = value;
                } else {
                    List<Float> list = getParseValue(value, Float.class);
                    obj[position] = list == null ? null : list.toArray(new Float[0]);
                }
                break;
            }
            case "double[]": {
                if (value == null || value instanceof double[]) {
                    obj[position] = value;
                } else {
                    List<Double> list = getParseValue(value, Double.class);
                    obj[position] = list == null ? null : list.toArray(new Double[0]);
                }
                break;
            }
            case "long[]": {
                if (value == null || value instanceof long[]) {
                    obj[position] = value;
                } else {
                    List<Long> list = getParseValue(value, Long.class);
                    obj[position] = list == null ? null : list.toArray(new Long[0]);
                }
                break;
            }
            case "string[]": {
                if (value == null || value instanceof String[]) {
                    obj[position] = value;
                } else {
                    List<String> list = getParseValue(value, String.class);
                    obj[position] = list == null ? null : list.toArray(new String[0]);
                }
                break;
            }
            case "map<string, object>":
            case "map":
            case "hashmap<string, object>":
            case "hashmap":
                if (value == null || value instanceof Map) {
                    obj[position] = value;
                } else {
                    obj[position] = getParseValue(value, HashMap.class);
                }
                break;
            case "object":
                obj[position] = value;
                break;
            default:
                //类型未匹配但实际value类型为json数据
                matchConformList(obj, position, arg);
                break;
        }
    }

    private void matchConformList(Object[] obj, int position, ArgumentsProperty arg) {
        //List<?>
        String childType = ValidUtils.match("(?<=<)(.*?)(?>=)", arg.getType()).trim();
        if (!TextUtils.isEmpty(childType)) {
            switch (childType) {
                case "Integer":
                    obj[position] = getParseValue(arg.getValue(), Integer.class);
                    break;
                case "Float":
                    obj[position] = getParseValue(arg.getValue(), Float.class);
                    break;
                case "Double":
                    obj[position] = getParseValue(arg.getValue(), Double.class);
                    break;
                case "Long":
                    obj[position] = getParseValue(arg.getValue(), Long.class);
                    break;
                case "String":
                    obj[position] = getParseValue(arg.getValue(), String.class);
                    break;
                default:
                    obj[position] = arg.getValue();
                    break;
            }
        } else {
            obj[position] = arg.getValue();
        }
    }

    private <T> List<T> getParseValue(Object rawValue, Class<T> clazz) {
        String json = (rawValue == null ? "" : String.valueOf(rawValue));
        if (!TextUtils.isEmpty(json)) {
            return JsonUtils.parseArray(json, clazz);
        }
        return null;
    }
}
