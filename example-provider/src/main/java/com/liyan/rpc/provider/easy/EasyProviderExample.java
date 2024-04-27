package com.liyan.rpc.provider.easy;

import com.liyan.rpc.common.service.UserService;
import com.liyan.rpc.provider.service.impl.UserServiceImpl;
import com.liyan.rpc.registry.LocalRegistry;
import com.liyan.rpc.server.VertxHttpServer;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {
    public static void main(String[] args) throws Exception {
        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        //启动服务
        VertxHttpServer server = new VertxHttpServer();
        server.start(8090);

    }

}
