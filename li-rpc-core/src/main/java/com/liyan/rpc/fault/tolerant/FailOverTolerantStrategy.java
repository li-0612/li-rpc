package com.liyan.rpc.fault.tolerant;

import com.liyan.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 降级到其他服务-容错策略
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        //todo 可自行扩展，获取其他服务的节点并调用
        return null;
    }
}
