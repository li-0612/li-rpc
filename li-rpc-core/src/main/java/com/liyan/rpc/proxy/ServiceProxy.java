package com.liyan.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.liyan.rpc.RpcApplication;
import com.liyan.rpc.config.RpcConfig;
import com.liyan.rpc.constant.RpcConstant;
import com.liyan.rpc.fault.retry.RetryStrategy;
import com.liyan.rpc.fault.retry.RetryStrategyFactory;
import com.liyan.rpc.fault.tolerant.TolerantStrategy;
import com.liyan.rpc.fault.tolerant.TolerantStrategyFactory;
import com.liyan.rpc.loadbalancer.LoadBalancer;
import com.liyan.rpc.loadbalancer.LoadBalancerFactory;
import com.liyan.rpc.model.RpcRequest;
import com.liyan.rpc.model.RpcResponse;
import com.liyan.rpc.model.ServiceMetaInfo;
import com.liyan.rpc.protocol.*;
import com.liyan.rpc.registry.Registry;
import com.liyan.rpc.registry.RegistryFactory;
import com.liyan.rpc.serializer.JdkSerializer;
import com.liyan.rpc.serializer.Serializer;
import com.liyan.rpc.serializer.SerializerFactory;
import com.liyan.rpc.server.tcp.VertxTcpClient;
import com.liyan.rpc.spi.SpiLoader;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 动态代理
 */
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        //指定序列化器
//        Serializer serializer = new JdkSerializer();
        //获取配置文件中的序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
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
            //发送请求  http://localhost:8090

            //从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();

            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            //初始化
//            registry.init(rpcConfig.getRegistryConfig());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(method.getDeclaringClass().getName());
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
//            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
//            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            List<ServiceMetaInfo> infos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(infos)) {
                throw new RuntimeException("暂无服务地址");
            }
            ServiceMetaInfo metaInfo = infos.get(0);

            //负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", request.getMethodName());
            ServiceMetaInfo info = loadBalancer.select(requestParams, infos);
            //发送tcp请求
            //使用重试机制
            RpcResponse rpcResponse;
            try {
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                rpcResponse = retryStrategy.dorRetry(() ->
                        VertxTcpClient.doRequest(request, info)
                );
//            RpcResponse rpcResponse = VertxTcpClient.doRequest(request, info);

            } catch (Exception e) {
                //容错机制
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                rpcResponse =  tolerantStrategy.doTolerant(requestParams, e);
            }
            return rpcResponse.getData();
            //发送http请求
//            try (HttpResponse response = HttpRequest
//                    .post(metaInfo.getServiceAddress()).body(bytes).execute()) {
//                 resBytes = response.bodyBytes();
//                isOk = response.isOk();
//            }
//
//            if (isOk) {
//                //反序列化
//                RpcResponse rpcResponse = serializer.deserialize(resBytes, RpcResponse.class);
//                return rpcResponse.getData();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
