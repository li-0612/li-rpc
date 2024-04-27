package com.liyan.rpc.lirpcspringbootstarter.bootstrap;

import com.liyan.rpc.RpcApplication;
import com.liyan.rpc.config.RpcConfig;
import com.liyan.rpc.lirpcspringbootstarter.annotation.EnableRpc;
import com.liyan.rpc.server.tcp.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * rpc框架启动
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //获取enablerpc注解
        boolean enableRpc = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()).get("needServer");

        //rpc 框架初始化（配置和注册中心）
        RpcApplication.init();

        //全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //启动服务器
        if (enableRpc) {
            VertxTcpServer server = new VertxTcpServer();
            server.start(rpcConfig.getServerPort());
        } else {
            log.info("rpc server not start");
        }
//        ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);
    }
}
