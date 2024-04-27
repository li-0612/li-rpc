package com.liyan.rpc.fault.tolerant;

import com.liyan.rpc.fault.retry.FixedIntervalRetryStrategy;
import com.liyan.rpc.fault.retry.RetryStrategy;
import com.liyan.rpc.fault.retry.RetryStrategyKeys;
import com.liyan.rpc.spi.SpiLoader;

public class TolerantStrategyFactory {
    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认重试策略
     */
    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailFastTolerantStrategy();

    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }

    public static void main(String[] args) {
        TolerantStrategy instance = getInstance(TolerantStrategyKeys.FAIL_FAST);
        System.out.println("instance = " + instance);

    }
}
