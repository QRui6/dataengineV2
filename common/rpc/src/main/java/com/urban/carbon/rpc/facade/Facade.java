package com.urban.carbon.rpc.facade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC 中被调用方会使用到的注解
 *  基于 AOP + 自定义注解实现统一的参数校验、异常捕获、日志打印、耗时统计、出入参的映射
 *  目的是让被调用方知道, 别人是如何调用我的工具的, 并方便进行打点和记录
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Facade {
}

