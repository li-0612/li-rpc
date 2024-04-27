package com.liyan.rpc.fault.tolerant;

import com.liyan.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略
 */
public interface TolerantStrategy {
    /**
     * 执行容错策略
     * @param context 上下文
     * @param e 异常
     * @return
     */
    RpcResponse doTolerant(Map<String,Object> context, Exception e);
}
