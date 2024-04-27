package com.liyan.rpc.serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liyan.rpc.model.RpcRequest;
import com.liyan.rpc.model.RpcResponse;

import java.io.IOException;

public class JsonSerializer implements Serializer{
    private static final ObjectMapper objectMapper  = new ObjectMapper();


    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        return objectMapper.writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        T obj = objectMapper.readValue(bytes, clazz);
        if (obj instanceof RpcRequest) {
            return handleRequest((RpcRequest) obj, clazz);
        }
        if (obj instanceof RpcResponse) {
            return handleResponse((RpcResponse) obj, clazz);
        }
        return obj;
    }

    /**
     *  由于 Object 的原始对象会被擦除，导致反序列化时会被作为 LinkedHashMap 无法转换成原始对象，因此这里做了特殊处理
     * @param request
     * @param clazz
     * @return
     * @param <T>
     * @throws IOException
     */
    private <T> T handleRequest(RpcRequest request, Class<T> clazz) throws IOException {
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] args = request.getArgs();
        //循环处理每个参数的类型
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> clazz1 = parameterTypes[i];
                //如果类型不同 则重新处理一下类型
                if (!clazz1.isAssignableFrom(args[i].getClass())) {
                    byte[] bytes = objectMapper.writeValueAsBytes(args[i]);
                    args[i] = objectMapper.readValue(bytes, clazz1);
                }
            }
        }

        return clazz.cast(request);
    }

    /**
     *  由于 Object 的原始对象会被擦除，导致反序列化时会被作为 LinkedHashMap 无法转换成原始对象，因此这里做了特殊处理
     * @param response
     * @param clazz
     * @return
     * @param <T>
     * @throws IOException
     */
    private <T> T handleResponse(RpcResponse response, Class<T> clazz) throws IOException {
        //处理响应数据
        byte[] asBytes = objectMapper.writeValueAsBytes(response.getData());
        response.setData(objectMapper.readValue(asBytes, response.getDataType()));
        return clazz.cast(response);
    }
}
