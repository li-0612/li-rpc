package com.liyan.rpc.provider.core;

import com.liyan.rpc.RpcApplication;
import com.liyan.rpc.bootstrap.ProviderBootstrap;
import com.liyan.rpc.common.service.UserService;
import com.liyan.rpc.config.RegistryConfig;
import com.liyan.rpc.config.RpcConfig;
import com.liyan.rpc.constant.RpcConstant;
import com.liyan.rpc.model.ServiceMetaInfo;
import com.liyan.rpc.model.ServiceRegisterInfo;
import com.liyan.rpc.provider.service.impl.UserServiceImpl;
import com.liyan.rpc.registry.LocalRegistry;
import com.liyan.rpc.registry.Registry;
import com.liyan.rpc.registry.RegistryFactory;
import com.liyan.rpc.server.VertxHttpServer;
import com.liyan.rpc.server.tcp.VertxTcpServer;
import com.liyan.rpc.spi.SpiLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoreProviderExample {
    public static void main(String[] args) {
       //要注册的服务列表
        List<ServiceRegisterInfo<?>> serviceRegisterInfos = new ArrayList<>();
        ServiceRegisterInfo<UserServiceImpl> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfos.add(serviceRegisterInfo);

        ProviderBootstrap.init(serviceRegisterInfos);

    }
}
