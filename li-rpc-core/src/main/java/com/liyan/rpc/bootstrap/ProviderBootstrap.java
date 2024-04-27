package com.liyan.rpc.bootstrap;

import com.liyan.rpc.RpcApplication;
import com.liyan.rpc.config.RegistryConfig;
import com.liyan.rpc.config.RpcConfig;
import com.liyan.rpc.model.ServiceMetaInfo;
import com.liyan.rpc.model.ServiceRegisterInfo;
import com.liyan.rpc.registry.LocalRegistry;
import com.liyan.rpc.registry.Registry;
import com.liyan.rpc.registry.RegistryFactory;
import com.liyan.rpc.server.tcp.VertxTcpServer;

import java.util.List;

public class ProviderBootstrap {
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        RpcApplication.init();
        //全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            //本地注册
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());
            //注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "服务注册失败", e);
            }
        }
        //启动服务
        VertxTcpServer server = new VertxTcpServer();
        server.start(rpcConfig.getServerPort());

    }

}
