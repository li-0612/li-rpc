package com.liyan.rpc.serializer;

import java.io.IOException;

public interface Serializer {
    /**
     * 序列化
     * @param obj
     * @return
     * @param <T>
     * @throws IOException
     */
   <T> byte[] serialize(T obj) throws IOException;

   /**
    * 反序列化
    * @param bytes
    * @param clazz
    * @param <T>
    * @return
    * @throws IOException
    */
   <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;
}
