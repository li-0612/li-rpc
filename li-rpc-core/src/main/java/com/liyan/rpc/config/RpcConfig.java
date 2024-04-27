package com.liyan.rpc.config;

import com.liyan.rpc.constant.SerializerKeys;
import com.liyan.rpc.fault.retry.RetryStrategyKeys;
import com.liyan.rpc.fault.tolerant.TolerantStrategyKeys;
import com.liyan.rpc.loadbalancer.LoadBalancer;
import com.liyan.rpc.loadbalancer.LoadBalancerKeys;
import lombok.Data;

/**
 * rpc 框架配置
 */
@Data
public class RpcConfig {

    /**
     * 服务名称
     */
    private String nam = "li-rpc";
    /**
     * 服务版本
     */
    private String version = "1.0";
    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";
    /**
     * 服务端口
     */
    private Integer serverPort = 8888;
    /**
     * 新增mock 模拟调用
     */
    private Boolean mock = false;

    /**
     * 序列化方式
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();
    /**
     * 负载均衡配置
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;
    /**
     * 重试策略配置
     */
    private String retryStrategy = RetryStrategyKeys.FIXED_INTERVAL;
    /**
     * 容错策略配置
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_OVER;
}
