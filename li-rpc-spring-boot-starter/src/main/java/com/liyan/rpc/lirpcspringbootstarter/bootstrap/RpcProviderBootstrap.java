package com.liyan.rpc.lirpcspringbootstarter.bootstrap;

import com.liyan.rpc.RpcApplication;
import com.liyan.rpc.config.RegistryConfig;
import com.liyan.rpc.config.RpcConfig;
import com.liyan.rpc.lirpcspringbootstarter.annotation.RpcService;
import com.liyan.rpc.model.ServiceMetaInfo;
import com.liyan.rpc.registry.LocalRegistry;
import com.liyan.rpc.registry.Registry;
import com.liyan.rpc.registry.RegistryFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * rpc 服务提供者启动
 */
public class RpcProviderBootstrap implements BeanPostProcessor {
    /**
     * bean初始化后执行，注册服务
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();


            // 注册服务
            //本地注册
            LocalRegistry.register(serviceName, beanClass);
            //全局配置
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            //注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "服务注册失败", e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
