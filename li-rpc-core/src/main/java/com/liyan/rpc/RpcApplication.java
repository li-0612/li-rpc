package com.liyan.rpc;

import com.liyan.rpc.config.RegistryConfig;
import com.liyan.rpc.config.RpcConfig;
import com.liyan.rpc.constant.RpcConstant;
import com.liyan.rpc.registry.Registry;
import com.liyan.rpc.registry.RegistryFactory;
import com.liyan.rpc.serializer.Serializer;
import com.liyan.rpc.serializer.SerializerFactory;
import com.liyan.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        //注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);

        //创建并注册shutdown hook，jvm退出时执行操作
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            registry.destroy();
        }));
    }

    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置 双层检测
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }



}
