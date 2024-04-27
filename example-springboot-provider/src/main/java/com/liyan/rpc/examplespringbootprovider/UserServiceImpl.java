package com.liyan.rpc.examplespringbootprovider;

import com.liyan.rpc.common.model.User;
import com.liyan.rpc.common.service.UserService;
import com.liyan.rpc.lirpcspringbootstarter.annotation.RpcService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名：="+user.getName());
        return user;
    }
}
