package com.liyan.rpc.consumer.core;

import com.liyan.rpc.RpcApplication;
import com.liyan.rpc.bootstrap.ConsumerBootstrap;
import com.liyan.rpc.common.model.User;
import com.liyan.rpc.common.service.UserService;
import com.liyan.rpc.config.RpcConfig;
import com.liyan.rpc.constant.RpcConstant;
import com.liyan.rpc.proxy.ServiceProxyFactory;
import com.liyan.rpc.utils.ConfigUtils;


public class CoreConsumerExample {
    public static void main(String[] args) {
//        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
//        System.out.println(rpcConfig);

        ConsumerBootstrap.init();

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("li-rpc-core-consume");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println("username" + newUser.getName());
        } else {
            System.out.println("newUser is null");
        }
        Integer number = userService.getNumber();
        System.out.println(number);
    }
}
