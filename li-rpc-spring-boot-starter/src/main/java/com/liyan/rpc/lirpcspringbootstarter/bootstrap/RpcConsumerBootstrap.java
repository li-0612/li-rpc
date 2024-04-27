package com.liyan.rpc.lirpcspringbootstarter.bootstrap;

import com.liyan.rpc.lirpcspringbootstarter.annotation.RpcReference;
import com.liyan.rpc.proxy.ServiceProxy;
import com.liyan.rpc.proxy.ServiceProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * rpc 服务消费者启动
 */
public class RpcConsumerBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        for (Field field : beanClass.getDeclaredFields()) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                //为属性生成代理对象
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);

                try {
                    field.set(bean, proxyObject);
                    field.setAccessible(false);
                } catch (Exception e) {
                    throw new RuntimeException("为字段注入代理对象失败",e);
                }
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
