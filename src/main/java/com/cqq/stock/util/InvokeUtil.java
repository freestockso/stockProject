package com.cqq.stock.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.util.Optional.ofNullable;

/**
 * 反射工具
 *
 * @author qiqi.chen
 */
@Slf4j
public class InvokeUtil {
    public static final String GET_METHOD_PREFIX = "get";
    public static final String SET_METHOD_PREFIX = "set";

    private InvokeUtil() {

    }

    public static String getGetMethodValue(Object obj, Field f) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (f.getName().length() == 0) {
            return null;
        }
        String getMethodName = GET_METHOD_PREFIX + firstLetterUpper(f.getName());
        Method declaredMethod = obj.getClass().getDeclaredMethod(getMethodName);
        Object invoke = declaredMethod.invoke(obj);
        return ofNullable(invoke).map(Object::toString).orElse(null);
    }

    public static void setSetMethodValue(Object obj, Field f, Object value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (f.getName().length() == 0) {
            return;
        }
        String getMethodName = SET_METHOD_PREFIX + firstLetterUpper(f.getName());
        Method declaredMethod = obj.getClass().getDeclaredMethod(getMethodName, f.getType());
        declaredMethod.invoke(obj, value);
    }

    public static String firstLetterUpper(String value) {
        if (value == null) {
            return null;
        }
        if (value.length() == 0) {
            return null;
        }
        if (value.length() == 1) {
            return value.toUpperCase();
        }
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
