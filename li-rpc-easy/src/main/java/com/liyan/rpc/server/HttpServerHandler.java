package com.liyan.rpc.server;

import com.liyan.rpc.model.RpcRequest;
import com.liyan.rpc.model.RpcResponse;
import com.liyan.rpc.registry.LocalRegistry;
import com.liyan.rpc.serializer.JdkSerializer;
import com.liyan.rpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

public class HttpServerHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request) {
        //指定序列化器
        final Serializer jdkSerializer = new JdkSerializer();

        //记录日志
        System.out.println("收到请求：" + request.method() + "-" + request.uri());
        //异步处理 http请求
        request.bodyHandler(body -> {

            //反序列化
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = jdkSerializer.deserialize(body.getBytes(), RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            //如果请求为null直接返回
            if (rpcRequest == null) {
                rpcResponse.setData("请求参数为空");
                doResponse(request, rpcResponse, jdkSerializer);
                return;
            }
            //获取服务 通过反射调用
            final Class<?> service = LocalRegistry.get(rpcRequest.getServiceName());
            //反射调用
            try {
                Method method = service.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                final Object result = method
                        .invoke(service.newInstance(), rpcRequest.getArgs());
                //封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("调用成功");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage("调用失败");
                rpcResponse.setException(e);
            }
            doResponse(request, rpcResponse, jdkSerializer);

        });
    }


    /**
     * 响应
     * @param request
     * @param rpcResponse
     * @param jdkSerializer
     */
    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer jdkSerializer) {

        HttpServerResponse serverResponse = request.response().putHeader("Content-Type", "application/json");
        //序列化
        try {
            final byte[] bytes = jdkSerializer.serialize(rpcResponse);
            serverResponse.end(Buffer.buffer(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            serverResponse.end(Buffer.buffer());
        }

    }

}
