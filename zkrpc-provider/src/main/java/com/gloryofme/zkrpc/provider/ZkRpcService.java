package com.gloryofme.zkrpc.provider;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务注解
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
@Target({ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Component
public @interface ZkRpcService {

    Class<?> value();

    String version() default "";
}
