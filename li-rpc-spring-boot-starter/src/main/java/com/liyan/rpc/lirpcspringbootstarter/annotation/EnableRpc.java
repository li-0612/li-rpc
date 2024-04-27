package com.liyan.rpc.lirpcspringbootstarter.annotation;

import com.liyan.rpc.lirpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import com.liyan.rpc.lirpcspringbootstarter.bootstrap.RpcInitBootstrap;
import com.liyan.rpc.lirpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启动rpc
 */
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ RpcInitBootstrap.class, RpcConsumerBootstrap.class, RpcProviderBootstrap.class})
public @interface EnableRpc {
    /**
     * 需要启动server
     * @return
     */
    boolean needServer() default true;
}
