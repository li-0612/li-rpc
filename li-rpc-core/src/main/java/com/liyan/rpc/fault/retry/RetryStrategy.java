package com.liyan.rpc.fault.retry;

import com.liyan.rpc.model.RpcRequest;
import com.liyan.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略
 */
public interface RetryStrategy {
    /**
     * 重试
     * @param callable
     * @return
     * @throws Exception
     */
    RpcResponse dorRetry(Callable<RpcResponse> callable) throws Exception;

}
