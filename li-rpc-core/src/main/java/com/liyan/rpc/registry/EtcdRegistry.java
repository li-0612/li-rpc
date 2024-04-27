package com.liyan.rpc.registry;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.liyan.rpc.config.RegistryConfig;
import com.liyan.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Slf4j
public class EtcdRegistry implements Registry {


    private Client client;
    private KV kvClient;

    /**
     * 跟节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";
    /**
     * 本机注册节点key集合（用于维护续期）
     */
    private static final Set<String> localRegistryNodeKeySet = new HashSet<>();

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 监听的key集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();


    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
        // 启动定时任务，定时刷新本地缓存
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception{

        //创建lease和kv客户端
        Lease leaseClient = client.getLeaseClient();
        //创建一个30秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        //设置要存储的键值对
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registryKey,StandardCharsets.UTF_8);
        log.info("registryKey={}",registryKey);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo),StandardCharsets.UTF_8);
        //将键值对跟租约关联起来，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key,value,putOption).get();
        //添加节点信息到本地缓存
        localRegistryNodeKeySet.add(registryKey);
    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registryKey,StandardCharsets.UTF_8));
        localRegistryNodeKeySet.remove(registryKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceName) {
        //优先从缓存获取服务
        List<ServiceMetaInfo> serviceMetaInfoList = registryServiceCache.readCache();
        if (CollUtil.isNotEmpty(serviceMetaInfoList)){
            return serviceMetaInfoList;
        }
        //获取服务列表
        //前缀搜索 结尾一定要加 “/"
        String searchPrefix = ETCD_ROOT_PATH + serviceName + "/";
        log.info("searchPrefix = {}", searchPrefix);
        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> kvs = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                    getOption)
                    .get()
                    .getKvs();
            log.info("kvs={}", JSONUtil.toJsonStr(kvs));
            //解析服务信息
            List<ServiceMetaInfo> serviceMetaInfos = kvs.stream().map(kv -> {
                String key = kv.getKey().toString(StandardCharsets.UTF_8);
                //监听key的变化
                watch(key);
                String value = kv.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList());
            //写入缓存
            registryServiceCache.writeCache(serviceMetaInfos);
            return serviceMetaInfos;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败",e);
        }
    }

    @Override
    public void destroy() {
        log.info("当前节点下线");
        //遍历本节点所以得key
        for (String key : localRegistryNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8));
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败", e);
            }
        }
        if (client != null) {
            client.close();
        }
        if (kvClient != null) {
            kvClient.close();
        }
    }

    @Override
    public void heartBeat() {
        //10秒续签一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                //遍历本节点所有的key
                for (String key : localRegistryNodeKeySet) {
                    try {
                        List<KeyValue> kvs = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8)).get().getKvs();
                        //该节点已过期（需要重启节点才能重新注册）
                        if (CollUtil.isEmpty(kvs)) {
                            continue;
                        }
                        //节点未过期，重新注册（相当于续签）
                        KeyValue keyValue = kvs.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo metaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        log.info("续期信息：{}", metaInfo);

                        register(metaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });
        //支持秒级定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        //之前未被监听，开启监听
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch){
            //监听节点
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8),response->{
                System.out.println("监听到节点变化");
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        case PUT:
                            System.out.println("节点更新");
                            break;
                        case DELETE:
                            System.out.println("节点删除");
                            registryServiceCache.clearCache();
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    public static void main(String[] args) throws Exception {
        // create client using endpoints
        Client client = Client.builder().endpoints("http://localhost:2379").build();

        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from("/rpc/com.liyan.rpc.common.service.UserService:1.0.0localhost:8888".getBytes(StandardCharsets.UTF_8));
//        ByteSequence value = ByteSequence.from("test_value".getBytes(StandardCharsets.UTF_8));

//        kvClient.put(key, value).get();
        CompletableFuture<GetResponse> completableFuture = kvClient.get(key);
        GetResponse getResponse = completableFuture.get();
        for (KeyValue kv : getResponse.getKvs()) {
            ByteSequence key1 = kv.getKey();
            ByteSequence value1 = kv.getValue();
            System.out.println(key1.toString(StandardCharsets.UTF_8) + ":" + value1.toString(StandardCharsets.UTF_8));
        }
        String searchPrefix = ETCD_ROOT_PATH + "com.liyan.rpc.common.service.UserService:1.0.0localhost";
        GetOption getOption = GetOption.builder().isPrefix(true).build();
        List<KeyValue> kvs = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                        getOption)
                .get()
                .getKvs();
        List<ServiceMetaInfo> collect = kvs.stream().map(kv -> {
            String value = kv.getValue().toString(StandardCharsets.UTF_8);
            return JSONUtil.toBean(value, ServiceMetaInfo.class);
        }).collect(Collectors.toList());
        System.out.println("collect = " + collect);


//        kvClient.delete(key);

//        EtcdRegistry etcdRegistry = new EtcdRegistry();
//        etcdRegistry.init(new RegistryConfig());
//
//
//        List<ServiceMetaInfo> infos = etcdRegistry.serviceDiscovery("/rpc/com.liyan.rpc.common.service.UserService:1.0.0localhost:8888");
//        System.out.println("infos = " + infos);
//
//
//        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//
//        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
//        //初始化
//        registry.init(rpcConfig.getRegistryConfig());
//        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName("com.liyan.rpc.common.service.UserService");
//        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
//        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
//        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
//        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceNodeKey());
//        System.out.println("serviceMetaInfos = " + serviceMetaInfos);
    }
}
