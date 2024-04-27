package com.liyan.rpc.fault.retry;

import com.liyan.rpc.spi.SpiLoader;

public class RetryStrategyFactory {
    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认重试策略
     */
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new FixedIntervalRetryStrategy();

    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }

    public static void main(String[] args) {
        RetryStrategy retryStrategy = getInstance(RetryStrategyKeys.FIXED_INTERVAL);
        System.out.println("retryStrategy = " + retryStrategy);

    }
}
