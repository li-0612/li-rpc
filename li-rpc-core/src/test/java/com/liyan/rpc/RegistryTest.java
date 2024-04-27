package com.liyan.rpc;

import com.liyan.rpc.config.RegistryConfig;
import com.liyan.rpc.model.ServiceMetaInfo;
import com.liyan.rpc.registry.EtcdRegistry;
import com.liyan.rpc.registry.Registry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class RegistryTest {
   Registry registry =  new EtcdRegistry();
    @Before
    public void init() throws Exception {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");
        registry.init(registryConfig);
    }
    @Test
    public void testRegister() throws Exception {
//        init();
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(9099);
        serviceMetaInfo.setServiceVersion("1.2.3");
        registry.register(serviceMetaInfo);

        Thread.sleep(60 * 1000);
    }


    @Test
    public void serviceDiscovery() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
//        serviceMetaInfo.setServiceHost("localhost");
//        serviceMetaInfo.setServicePort(9099);
        serviceMetaInfo.setServiceVersion("1.2.3");
        String serviceKey = serviceMetaInfo.getServiceKey();
        List<ServiceMetaInfo> infos = registry.serviceDiscovery(serviceKey);
        Assert.assertNotNull(infos);
        System.out.println(infos);
    }
}
