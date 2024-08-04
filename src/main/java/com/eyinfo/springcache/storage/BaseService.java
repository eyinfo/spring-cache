package com.eyinfo.springcache.storage;

import com.eyinfo.foundation.events.Action3;
import com.eyinfo.foundation.utils.ObjectJudge;
import com.eyinfo.springcache.storage.invoke.*;

import java.util.List;

/**
 * @Author lijinghuan
 * @Email:ljh0576123@163.com
 * @CreateTime:2020/9/13
 * @Description:
 * @Modifier:
 * @ModifyContent:
 */
class BaseService {

    private <Dao> InvokeResult invoke(Dao dao, DbMethodEntry methodEntry, Action3<List<ArgumentsProperty>, Object[], Boolean> propertyCall, Object... values) {
        InvokeHelper helper = new InvokeHelper();
        InvokeProperty property = new InvokeProperty();
        FunctionProperty functionProperty = new FunctionProperty();
        functionProperty.setName(methodEntry.getMethodName());
        List<ArgumentsProperty> propertyArgs = functionProperty.getArgs();
        propertyCall.call(propertyArgs, values, !ObjectJudge.isNullOrEmpty(methodEntry.getParameterTypes()));
        property.setFunctions(functionProperty);
        return helper.invoke(dao, property);
    }

    protected <Dao> InvokeResult invokeWithString(Dao dao, DbMethodEntry methodEntry, String where) {
        return this.invoke(dao, methodEntry, (arguments, values, hasParams) -> {
            if (!hasParams) {
                return;
            }
            ArgumentsProperty argumentsProperty = new ArgumentsProperty();
            argumentsProperty.setType("String");
            argumentsProperty.setValue(values[0]);
            arguments.add(argumentsProperty);
        }, where);
    }

    protected <Dao> InvokeResult invoke(Dao dao, DbMethodEntry methodEntry, Object... values) {
        return this.invoke(dao, methodEntry, (arguments, values1, hasParams) -> {
            if (!hasParams) {
                return;
            }
            Class<?>[] parameterTypes = methodEntry.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                ArgumentsProperty argumentsProperty = new ArgumentsProperty();
                argumentsProperty.setTypeClass(parameterType);
                if (!ObjectJudge.isNullOrEmpty(values1) && i < values1.length) {
                    argumentsProperty.setValue(values1[i]);
                }
                arguments.add(argumentsProperty);
            }
        }, values);
    }
}
