package com.liyan.rpc.serializer;

import com.liyan.rpc.constant.SerializerKeys;
import com.liyan.rpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化工厂
 */
public class SerializerFactory {
    static {
        SpiLoader.load(Serializer.class);
    }
    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>() {{
        put(SerializerKeys.JDK, new JdkSerializer());
        put(SerializerKeys.HESSIAN, new HessianSerializer());
        put(SerializerKeys.KRYO, new KryoSerializer());
        put(SerializerKeys.JSON, new JsonSerializer());
    }};

    /**
     * 默认序列化器
     */
    private static final Serializer defaultSerializer = KEY_SERIALIZER_MAP.get(SerializerKeys.JDK);

    /**
     * 获取实例
     */
    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }

    public static void main(String[] args) {
        Serializer instance = getInstance(SerializerKeys.JDK);
        System.out.println(instance);
    }

}
