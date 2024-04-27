package com.liyan.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.liyan.rpc.model.RpcResponse;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间重试策略
 */
public class FixedIntervalRetryStrategy implements RetryStrategy{

    @Override
    public RpcResponse dorRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(3l, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println("重试次数：" + attempt.getAttemptNumber());
                    }
                })
                .build();
        return retryer.call(callable);
    }
}
