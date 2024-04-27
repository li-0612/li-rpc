package com.liyan.rpc.loadbalancer;

import com.liyan.rpc.spi.SpiLoader;

/**
 * 负载均衡工厂
 */
public class LoadBalancerFactory {
    static {
        SpiLoader.load(LoadBalancer.class);
    }
    /**
     * 默认负载均衡器
     */
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    /**
     * 获取实例
     * @param key 负载均衡器键名
     * @return
     */
    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }

    public static void main(String[] args) {
        System.out.println(getInstance(LoadBalancerKeys.ROUND_ROBIN));
    }
}
