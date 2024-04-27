package com.liyan.rpc.fault.retry;

import com.liyan.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 不重试策略
 */
public class NoRetryStrategy implements RetryStrategy{

    @Override
    public RpcResponse dorRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
