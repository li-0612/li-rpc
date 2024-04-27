package com.liyan.rpc.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户
 */
public class User implements Serializable {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
