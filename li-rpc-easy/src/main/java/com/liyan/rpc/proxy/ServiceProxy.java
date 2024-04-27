package com.liyan.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.liyan.rpc.model.RpcRequest;
import com.liyan.rpc.model.RpcResponse;
import com.liyan.rpc.serializer.JdkSerializer;
import com.liyan.rpc.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理
 */
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //指定序列化器
        Serializer serializer = new JdkSerializer();
        //构造请求
        RpcRequest request = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
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
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
