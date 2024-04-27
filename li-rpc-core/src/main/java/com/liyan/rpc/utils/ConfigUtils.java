package com.liyan.rpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.liyan.rpc.config.RpcConfig;

/**
 * 配置工具类
 */
public class ConfigUtils {
    public static String getConfig(String key) {
        return System.getProperty(key);
    }

    /**
     * 加载配置对象
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix) {

        return loadConfig(clazz, prefix, "");
    }

    /**
     * 加载配置对象，支持区分环境
     *
     * @param clazz
     * @param prefix
     * @param environment
     * @param <T>
     * @return
     */
    private static <T> T loadConfig(Class<T> clazz, String prefix, String environment) {

        StringBuilder configBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configBuilder.append("-").append(environment);
        }
        configBuilder.append(".properties");
        Props props = new Props(configBuilder.toString());
        return props.toBean(clazz, prefix);
    }

}
