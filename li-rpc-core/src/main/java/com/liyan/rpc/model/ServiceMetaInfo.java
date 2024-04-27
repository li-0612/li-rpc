package com.liyan.rpc.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 服务注册元信息
 */
@Data
public class ServiceMetaInfo {
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务版本
     */
    private String serviceVersion;
    /**
     * 服务地址
     */
    private String serviceHost;

    /**
     * 服务端口
     */
    private int servicePort;
    /**
     * 服务分组
     */
    private String serviceGroup = "default";

    /**
     * 获取服务键名
     */
    public String getServiceKey() {
//        return serviceName + ":" + serviceVersion + ":" + serviceGroup;
        return serviceName + ":" + serviceVersion;
    }
    /**
     * 获取服务注册节点键名
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }

    /**
     * 获取完整服务地址
     */
    public String getServiceAddress() {
        if (StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return  serviceHost + ":" + servicePort;
    }
}
