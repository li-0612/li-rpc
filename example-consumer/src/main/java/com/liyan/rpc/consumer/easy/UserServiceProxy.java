package com.liyan.rpc.consumer.easy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.liyan.rpc.common.model.User;
import com.liyan.rpc.common.service.UserService;
import com.liyan.rpc.model.RpcRequest;
import com.liyan.rpc.model.RpcResponse;
import com.liyan.rpc.serializer.JdkSerializer;
import com.liyan.rpc.serializer.Serializer;

import java.io.IOException;

/**
 * 静态代理
 */
public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        //指定序列化器
        Serializer serializer = new JdkSerializer();
        //构造请求
        RpcRequest request = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();
        try {
            //序列化
            byte[] bytes = serializer.serialize(request);
            byte[] resBytes;
            boolean isOk = false;
            //发送请求
            try (HttpResponse response = HttpRequest
                    .post("http://localhost:8090").body(bytes).execute()) {
                resBytes = response.bodyBytes();
                isOk = response.isOk();
            }

            if (isOk) {
                //反序列化
                RpcResponse rpcResponse = serializer.deserialize(resBytes, RpcResponse.class);
                return (User) rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
