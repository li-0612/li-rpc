package com.liyan.rpc.registry;

import com.liyan.rpc.constant.SerializerKeys;
import com.liyan.rpc.serializer.*;
import com.liyan.rpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化工厂
 */
public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);
    }


    /**
     * 默认序列化器
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取实例
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }

    public static void main(String[] args) {
        SpiLoader.loadAll();
//        Registry instance = getInstance("etcd");
//        System.out.println(instance);
    }

}
