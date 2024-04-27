package com.liyan.rpc.loadbalancer;

import cn.hutool.core.collection.CollUtil;
import com.liyan.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 随机负载均衡器
 */
public class RandomLoadBalancer implements LoadBalancer{
    /**
     * 当前轮询的下标
     */
    private final Random random = new Random();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            return null;
        }
        //只有一个服务无需随机
        int size = serviceMetaInfoList.size();
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }
        //取模算法轮询
        return serviceMetaInfoList.get(random.nextInt(serviceMetaInfoList.size()));
    }
}
