package com.liyan.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MockServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //根据方法的返回值类型 生成特定的默认值对象
        Class<?> returnType = method.getReturnType();
        return getDefaultObject(returnType);
    }

    /**
     * 生成指定类型的默认对象值
     * @param type
     * @return
     */
    private Object getDefaultObject(Class<?> type) {

        if (type.isPrimitive()) {
            //基本类型
            if (type == int.class) {
                return 0;
            }else if (type == long.class) {
                return 0L;
            }else if (type == float.class) {
                return 0.0f;
            } else if (type == double.class) {
                return 0.0d;
            }else if (type == boolean.class) {
                return false;
            }else if (type == char.class) {
                return '\u0000';
            }else if (type == short.class) {
                return 0;
            }else if (type == byte.class) {
                return 0;
            }
        }
        //对象类型
        return null;
    }
}
