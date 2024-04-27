package com.liyan.rpc.registry;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简易版本地注册中心
 */
@Slf4j
public class LocalRegistry {
    /**
     * 注册信息存储
     */
    private static final Map<String, Class<?>> REGISTRY_INFO = new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param serviceName 服务名称
     * @param clazz       服务实现类
     */
    public static void register(String serviceName, Class<?> clazz) {
        log.info("注册服务：" + serviceName + "，实现类：" + clazz);
        REGISTRY_INFO.put(serviceName, clazz);
    }

    /**
     * 获取服务
     * @param serviceName
     * @return
     */
    public static Class<?> get(String serviceName) {
        return REGISTRY_INFO.get(serviceName);
    }

    /**
     * 删除服务
     * @param serviceName
     * @return
     */
    public static Class<?> remove(String serviceName) {
        return REGISTRY_INFO.remove(serviceName);
    }
}
