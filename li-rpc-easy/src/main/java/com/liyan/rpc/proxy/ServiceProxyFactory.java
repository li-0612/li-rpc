package com.liyan.rpc.proxy;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂
 */
public class ServiceProxyFactory {
    /**
     * 根据服务类获取代理对象
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T getProxy(Class<T> clazz)
    {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new ServiceProxy());
    }
}
