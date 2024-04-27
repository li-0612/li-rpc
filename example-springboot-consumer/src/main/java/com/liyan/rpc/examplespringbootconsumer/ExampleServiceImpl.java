package com.liyan.rpc.examplespringbootconsumer;

import com.liyan.rpc.common.model.User;
import com.liyan.rpc.common.service.UserService;
import com.liyan.rpc.lirpcspringbootstarter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {
    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("liyan");
        User serviceUser = userService.getUser(user);
        System.out.println(serviceUser.getName());
    }
}
