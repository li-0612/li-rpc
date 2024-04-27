package com.liyan.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.liyan.rpc.serializer.Serializer;
import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
public class SpiLoader {
    public SpiLoader() {
        System.out.println("SpiLoader");
    }

    /**
     * 存储已加载的类：接口名=》（key=》实现类）
     */
    private static final Map<String, Map<String, Class<?>>> loadedMap = new ConcurrentHashMap<>();

    /**
     *      * 对象实例缓存（避免重复 new），类路径 => 对象实例，单例模式
     */
    public static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();
    /**
     *           * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";
    /**
     *           * 用户自定义 SPI 目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";
    /**
     *           * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};
    /**
     *           * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 加载所有类型
     */
    public static void loadAll() {
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }

    /**
     * 获取某个接口的实例
     * @param aClass
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<T> aClass,String key) {
        String className = aClass.getName();
        Map<String, Class<?>> classMap = loadedMap.get(className);
        if (classMap == null) {
            throw new RuntimeException(String.format("SpiLoader未找到%s的实现类", className));
        }
        if (!classMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader的%s不存在key=%s的类型", className, key));
        }
        //获取要加载的实现类型
        Class<?> implClass = classMap.get(key);
        //从实例缓存中加载指定类型的实例
        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.newInstance());
            } catch (Exception e) {
                String errMsg = String.format("%s类实例化失败", implClassName);
                throw new RuntimeException(errMsg, e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }

    /**
     * 加载指定类型
     * @param aClass
     * @return
     */
    public static Map<String, Class<?>> load(Class<?> aClass)  {
        log.info("加载类型为 {} 的 SPI", aClass.getName());
        //扫描路径，用户自定义的spi优先级高于系统默认的spi
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String scanDir : SCAN_DIRS) {
            String path = scanDir + aClass.getName(); ;
            List<URL> resources = ResourceUtil.getResources(path);
            //读取每个资源文件
            for (URL resource : resources) {
                //读取文件内容
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] strArray = line.split("=");
                        if (strArray.length > 1) {
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                } catch (Exception e) {
//                    log.error("load spi error", e);
                    e.printStackTrace();
                }

            }
        }
        loadedMap.put(aClass.getName(), keyClassMap);
        return keyClassMap;
    }

    public static void main(String[] args) {
        loadAll();
        System.out.println(loadedMap);
    }
}
