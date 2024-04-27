package com.liyan.rpc.loadbalancer;

import cn.hutool.core.collection.CollUtil;
import com.liyan.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 一致性哈希负载均衡器
 */
public class ConsistentHashLoadBalancer implements LoadBalancer{
    /**
     * 一致性hash环，存放虚拟节点
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualMap = new TreeMap<>();
    /**
     * 虚拟节点数
     */
    private final int virtualNodeCount = 100;

    /**
     * 一致性hash轮询
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 可用服务列表
     * @return
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            return null;
        }

        //构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < virtualNodeCount; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualMap.put(hash, serviceMetaInfo);
            }
        }
        //获取请求调用的hash值
        int hash = getHash(requestParams);
        //选择最接近且大于等于调用请求hash值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualMap.ceilingEntry(hash);
        if (entry == null) {
            //如果没有大于等于调用请求hash值的虚拟节点，则返回环首部的节点
            entry = virtualMap.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * 获取hash值 hash算法可自行实现
     * @param key
     * @return
     */
    private int getHash(Object key) {
        return key.hashCode();
    }
}
