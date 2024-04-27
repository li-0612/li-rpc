package com.liyan.rpc.fault.retry;

import com.liyan.rpc.model.RpcResponse;
import org.junit.Test;

import java.util.concurrent.Callable;

public class RetryStrategyTest {
//    NoRetryStrategy noRetryStrategy = new NoRetryStrategy();
    FixedIntervalRetryStrategy fixedIntervalRetryStrategy = new FixedIntervalRetryStrategy();

    @Test
    public void testNoRetryStrategy() throws Exception {
        try {
            RpcResponse rpcResponse = fixedIntervalRetryStrategy.dorRetry(new Callable<RpcResponse>() {
                public RpcResponse call() throws Exception {
                    System.out.println("测试重试");
                    throw new Exception("模拟重试失败");
                }
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println(" 重试多次失败");
            e.printStackTrace();
        }


    }
}
