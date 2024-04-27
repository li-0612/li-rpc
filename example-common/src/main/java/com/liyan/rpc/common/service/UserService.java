package com.liyan.rpc.common.service;

import com.liyan.rpc.common.model.User;

public interface UserService {
    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);

    default int getNumber() {
        return 1;
    }
}
