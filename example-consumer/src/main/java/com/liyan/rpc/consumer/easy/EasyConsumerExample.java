package com.liyan.rpc.consumer.easy;

import com.liyan.rpc.common.model.User;
import com.liyan.rpc.common.service.UserService;
import com.liyan.rpc.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {
        //静态代理
//        UserService userService = new UserServiceProxy();
        //动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("li-rpc");
        //调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("newUser is null");
        }
    }
}
