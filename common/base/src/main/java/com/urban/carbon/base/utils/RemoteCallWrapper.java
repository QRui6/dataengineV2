package com.urban.carbon.base.utils;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.ImmutableSet;
import com.urban.carbon.base.exception.RemoteCallException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import static com.urban.carbon.base.exception.BizErrorCode.REMOTE_CALL_RESPONSE_IS_FAILED;
import static com.urban.carbon.base.exception.BizErrorCode.REMOTE_CALL_RESPONSE_IS_NULL;

/**
 * 远程调用包装工具类。
 *
 * <p>该类封装了远程调用的通用逻辑，包括：
 * - 调用耗时统计；
 * - 响应结果校验（如 success 字段）；
 * - 响应码校验（如 responseCode）；
 * - 异常统一处理与日志记录。</p>
 *
 * <p>通过函数式编程方式传入调用方法，支持灵活的远程服务调用封装。</p>
 *
 * <p><strong>使用方式示例：</strong></p>
 * <pre>{@code
 * UserResponse response = RemoteCallWrapper.call(userService::getUser, new UserRequest("123"));
 * }</pre>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Slf4j
public class RemoteCallWrapper {

    /**
     * 成功标志字段的常见方法名称集合。
     * 用于反射查找响应对象中表示“是否成功”的方法。
     */
    private static final ImmutableSet<String> SUCCESS_CHECK_METHOD = ImmutableSet.of(
            "isSuccess", "isSucceeded", "getSuccess");

    /**
     * 响应码字段的方法名称集合。
     * 用于反射查找响应对象中的响应码字段。
     */
    private static final ImmutableSet<String> SUCCESS_CODE_METHOD = ImmutableSet.of(
            "getResponseCode");

    /**
     * 表示成功的响应码集合。
     * 用于判断响应码是否为合法的成功状态。
     */
    private static final ImmutableSet<String> SUCCESS_CODE = ImmutableSet.of(
            "SUCCESS", "DUPLICATE", "DUPLICATED_REQUEST");


    /**
     * 远程调用入口方法 1。
     *
     * <p>默认启用响应检查，不检查响应码。</p>
     *
     * @param function      调用的函数式接口
     * @param request       请求参数
     * @param checkResponse 是否检查响应是否成功
     * @param <T>           请求类型
     * @param <R>           响应类型
     * @return 返回远程调用结果
     */
    public static <T, R> R call(Function<T, R> function, T request, boolean checkResponse) {
        return call(function, request, request.getClass().getSimpleName(), checkResponse, false);
    }

    /**
     * 远程调用入口方法 2。
     *
     * <p>默认启用响应检查，不检查响应码。</p>
     *
     * @param function 调用的函数式接口
     * @param request  请求参数
     * @param <T>      请求类型
     * @param <R>      响应类型
     * @return 返回远程调用结果
     */
    public static <T, R> R call(Function<T, R> function, T request) {
        return call(function, request, request.getClass().getSimpleName(), true, false);
    }

    /**
     * 远程调用入口方法 3。
     *
     * <p>默认启用响应检查，不检查响应码。</p>
     *
     * @param function    调用的函数式接口
     * @param request     请求参数
     * @param requestName 请求名称（用于日志记录）
     * @param <T>         请求类型
     * @param <R>         响应类型
     * @return 返回远程调用结果
     */
    public static <T, R> R call(Function<T, R> function, T request, String requestName) {
        return call(function, request, requestName, true, false);
    }

    /**
     * 远程调用入口方法 4。
     *
     * <p>指定是否启用响应检查，不检查响应码。</p>
     *
     * @param function      调用的函数式接口
     * @param request       请求参数
     * @param requestName   请求名称（用于日志记录）
     * @param checkResponse 是否检查响应是否成功
     * @param <T>           请求类型
     * @param <R>           响应类型
     * @return 返回远程调用结果
     */
    public static <T, R> R call(Function<T, R> function, T request, String requestName,
                                boolean checkResponse) {
        return call(function, request, requestName, checkResponse, false);
    }

    /**
     * 远程调用入口方法 5。
     *
     * <p>指定是否启用响应检查和响应码检查。</p>
     *
     * @param function          调用的函数式接口
     * @param request           请求参数
     * @param checkResponse     是否检查响应是否成功
     * @param checkResponseCode 是否检查响应码是否为成功状态
     * @param <T>               请求类型
     * @param <R>               响应类型
     * @return 返回远程调用结果
     */
    public static <T, R> R call(Function<T, R> function, T request, boolean checkResponse,
                                boolean checkResponseCode) {
        return call(function, request, request.getClass().getSimpleName(), checkResponse, checkResponseCode);
    }

    /**
     * 远程调用内部实现方法。
     *
     * <p>执行远程调用并进行以下操作：
     * - 记录调用耗时；
     * - 检查响应是否为空或失败；
     * - 捕获异常并抛出统一的远程调用异常；
     * - 打印请求和响应日志。</p>
     *
     * @param function          调用的函数式接口
     * @param request           请求参数
     * @param requestName       请求名称（用于日志记录）
     * @param checkResponse     是否检查响应是否成功
     * @param checkResponseCode 是否检查响应码是否为成功状态
     * @param <T>               请求类型
     * @param <R>               响应类型
     * @return 返回远程调用结果
     * @throws IllegalArgumentException 当方法访问异常或目标异常发生时抛出
     */
    public static <T, R> R call(Function<T, R> function, T request, String requestName, boolean checkResponse,
                                boolean checkResponseCode) {
        StopWatch stopWatch = new StopWatch();
        R response = null;
        try {
            stopWatch.start();
            response = function.apply(request);
            stopWatch.stop();

            if (checkResponse) {
                Assert.notNull(response, REMOTE_CALL_RESPONSE_IS_NULL.name());
                if (!isResponseValid(response)) {
                    log.error("Response Invalid on Remote Call request {} , response {}",
                            JSON.toJSONString(request),
                            JSON.toJSONString(response));
                    throw new RemoteCallException(JSON.toJSONString(response), REMOTE_CALL_RESPONSE_IS_FAILED);
                }
            }

            if (checkResponseCode) {
                Assert.notNull(response, REMOTE_CALL_RESPONSE_IS_NULL.name());
                if (!isResponseCodeValid(response)) {
                    log.error("Response code Invalid on Remote Call request {} , response {}",
                            JSON.toJSONString(request),
                            JSON.toJSONString(response));
                    throw new RemoteCallException(JSON.toJSONString(response), REMOTE_CALL_RESPONSE_IS_FAILED);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Catch Exception on Remote Call :{}", e.getMessage(), e);
            throw new IllegalArgumentException("Catch Exception on Remote Call " + e.getMessage(), e);
        } catch (Throwable e) {
            log.error("request exception {}", JSON.toJSONString(request));
            log.error("Catch Exception on Remote Call :{}", e.getMessage(), e);
            throw e;
        } finally {
            if (log.isInfoEnabled()) {
                log.info("## Method = {} ,## Used times = {}ms ,## [request Body]:{},## [Response Body]:{}",
                        requestName, stopWatch.getTotalTimeMillis(),
                        JSON.toJSONString(request), JSON.toJSONString(response));
            }
        }

        return response;
    }

    /**
     * 判断响应对象是否表示成功状态（基于 success 字段）。
     *
     * <p>通过反射查找 success 字段对应的方法，并判断其值是否为 true。</p>
     *
     * @param response 响应对象
     * @param <R>      响应类型
     * @return 如果响应表示成功则返回 true，否则返回 false
     * @throws IllegalAccessException    方法不可访问时抛出
     * @throws InvocationTargetException 方法调用异常时抛出
     */
    private static <R> boolean isResponseValid(R response)
            throws IllegalAccessException, InvocationTargetException {
        Method successMethod = null;
        Method[] methods = response.getClass().getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (SUCCESS_CHECK_METHOD.contains(methodName)) {
                successMethod = method;
                break;
            }
        }
        if (successMethod == null) {
            return true;
        }

        return (Boolean) successMethod.invoke(response);
    }

    /**
     * 判断响应对象的响应码是否表示成功状态（基于 responseCode 字段）。
     *
     * <p>通过反射查找 responseCode 字段对应的方法，并判断其值是否在允许的成功码集合中。</p>
     *
     * @param response 响应对象
     * @param <R>      响应类型
     * @return 如果响应码表示成功则返回 true，否则返回 false
     * @throws IllegalAccessException    方法不可访问时抛出
     * @throws InvocationTargetException 方法调用异常时抛出
     */
    private static <R> boolean isResponseCodeValid(R response)
            throws IllegalAccessException, InvocationTargetException {
        Method successMethod = null;
        Method[] methods = response.getClass().getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (SUCCESS_CODE_METHOD.contains(methodName)) {
                successMethod = method;
                break;
            }
        }
        if (successMethod == null) {
            return true;
        }

        return SUCCESS_CODE.contains(successMethod.invoke(response));
    }
}

