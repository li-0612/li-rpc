package com.liyan.rpc.fault.retry;

/**
 * 重试策略常量
 */
public interface RetryStrategyKeys {
    /**
     * 不重试
     */
    String NO_RETRY = "no-retry";
    /**
     * 固定间隔重试
     */
    String FIXED_INTERVAL = "fixed-interval";
}
