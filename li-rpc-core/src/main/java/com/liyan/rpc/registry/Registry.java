package com.liyan.rpc.registry;

import com.liyan.rpc.config.RegistryConfig;
import com.liyan.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 注册中心
 */
public interface Registry {
    /**
     * 初始化
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务（服务端）
     *
     * @param serviceMetaInfo 服务元信息
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;
    /**
     * 注销服务
     */
    void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 获取服务
     * 服务发现（获取某服务的所有节点，消费端）
     *
     * @param serviceName 服务名称
     * @return 服务元信息
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceName) ;

    /**
     * 移除服务
     * @return 服务元信息
     */
    void destroy();

    /**
     * 心跳检测（服务端）
     */
    void heartBeat();

    /**
     * 监听服务节点变化
     * @param serviceNodeKey 服务节点key
     */
    void watch(String serviceNodeKey);

}
