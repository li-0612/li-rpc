package com.liyan.rpc.provider.service.impl;

import com.liyan.rpc.common.model.User;
import com.liyan.rpc.common.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名：="+user.getName());
        return user;
    }
}
