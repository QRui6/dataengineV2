package com.urban.carbon.rpc.facade;

import com.urban.carbon.base.exception.BizException;
import com.urban.carbon.base.exception.SystemException;
import com.urban.carbon.base.response.BaseResponse;
import com.urban.carbon.base.response.ResponseCode;
import com.urban.carbon.base.utils.BeanValidator;
import com.alibaba.fastjson2.JSON;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * FacadeAspect.java
 * <p>
 * 切面类，用于在调用标记了 @Facade 注解的方法前后进行统一的日志记录、参数校验和异常处理。
 * </p>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Aspect
@Component
@Slf4j
public class FacadeAspect {

    @Pointcut("@annotation(com.urban.carbon.rpc.facade.Facade)")
    public void pointcut() {}

    /**
     * 环绕通知方法，用于在调用标记了 @Facade 注解的方法前后进行统一的日志记录、参数校验和异常处理。
     *
     * @param pjp ProceedingJoinPoint 对象，包含了目标方法的信息和参数
     * @return 目标方法的执行结果或失败响应
     * @throws Exception 可能抛出的异常
     */
    @Around("pointcut()")
    public Object facade(ProceedingJoinPoint pjp) throws Exception {
        // 计时组件
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 获取需要调用的方法、参数
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args = pjp.getArgs();
        log.info("start to execute , method = {} , args = {}", method.getName(), JSON.toJSONString(args));
        Class<?> returnType = method.getReturnType();
        // 进行参数校验
        for (Object parameter : args) {
            try {
                BeanValidator.validateObject(parameter);
            } catch (ValidationException e) {
                printLog(stopWatch, method, args, "fail to validate", null, e);
                return getFailedResponse(returnType, e);
            }
        }
        // 执行目标方法
        try {
            Object response = pjp.proceed();
            enrichObject(response);
            printLog(stopWatch, method, args, "end to execute", response, null);
            return response;
        } catch (Throwable throwable) {
            // 如果执行异常, 则返回一个失败的response
            printLog(stopWatch, method, args, "failed to execute", null, throwable);
            return getFailedResponse(returnType, throwable);
        }
    }

    /**
     * 根据异常类型生成失败响应。
     *
     * @param returnType 返回值类型
     * @param throwable  抛出的异常
     * @return 失败响应对象
     * @throws NoSuchMethodException     如果找不到构造方法
     * @throws IllegalAccessException    如果构造方法不可访问
     * @throws InvocationTargetException 如果构造方法调用异常
     * @throws InstantiationException    如果实例化异常
     */
    private Object getFailedResponse(Class<?> returnType, Throwable throwable)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        log.warn("failed to getFailedResponse , throwable = {}", throwable.getClass().getName());
        // 如果返回值的类型为 BaseResponse 的子类, 则创建一个通用的失败响应
        if (returnType.getDeclaredConstructor().newInstance() instanceof BaseResponse response) {
            response.setSuccess(false);
            if (throwable instanceof BizException bizException) {
                response.setResponseMessage(bizException.getErrorCode().getMessage());
                response.setResponseCode(bizException.getErrorCode().getCode());
            } else if (throwable instanceof SystemException systemException) {
                response.setResponseMessage(systemException.getErrorCode().getMessage());
                response.setResponseCode(systemException.getErrorCode().getCode());
            } else {
                response.setResponseMessage(throwable.toString());
                response.setResponseCode(ResponseCode.BIZ_ERROR.name());
            }
            return response;
        }
        log.error(
                "failed to getFailedResponse , returnType ({}) is not instanceof BaseResponse",
                returnType);
        return null;
    }

    /**
     * 将response的信息补全, 主要是code.
     * 这里执行的目的在于：防止之后的代码因为 responseCode 为空而导致的 nullptrError 错误
     *
     * @param response 响应
     */
    private void enrichObject(Object response) {
        if (response instanceof BaseResponse) {
            if (((BaseResponse) response).getSuccess()) {
                // 如果状态是成功的, 需要将未设置的responseCode进行设置
                if (StringUtils.isEmpty(((BaseResponse) response).getResponseCode())) {
                    ((BaseResponse) response).setResponseCode(ResponseCode.SUCCESS.name());
                }
            } else {
                //如果状态是成功的, 需要将未设置的responseCode设置成BIZ_ERROR
                if (StringUtils.isEmpty(((BaseResponse) response).getResponseCode())) {
                    ((BaseResponse) response).setResponseCode(ResponseCode.BIZ_ERROR.name());
                }
            }
        }
    }

    /**
     * 日志打印
     *
     * @param stopWatch 耗时
     * @param method    方法
     * @param args      参数
     * @param action    行为
     * @param response  响应
     * @param throwable 需要抛出的异常
     */
    private void printLog(StopWatch stopWatch, Method method, Object[] args, String action, Object response, Throwable throwable) {
        try {
            //因为此处有JSON.toJSONString, 可能会有异常, 需要进行捕获, 避免影响主干流程
            log.info(getInfoMessage(stopWatch, method, args, action, response, throwable), throwable);
            // 如果校验失败, 则返回一个失败的response
        } catch (Exception e1) {
            log.error("log failed", e1);
        }
    }

    /**
     * 统一格式输出, 方便做日志统计
     * 如果调整此处的格式, 需要同步调整日志监控
     *
     * @param stopWatch 耗时
     * @param method    方法
     * @param args      参数
     * @param action    行为
     * @param response  响应
     * @return 拼接后的字符串
     */
    private String getInfoMessage(StopWatch stopWatch, Method method, Object[] args, String action, Object response,
                                  Throwable exception) {

        StringBuilder stringBuilder = new StringBuilder(action);
        stringBuilder.append(" ,method = ");
        stringBuilder.append(method.getName());
        stringBuilder.append(" ,cost = ");
        stringBuilder.append(stopWatch.getTime()).append(" ms");
        if (response instanceof BaseResponse) {
            stringBuilder.append(" ,success = ");
            stringBuilder.append(((BaseResponse) response).getSuccess());
        }
        if (exception != null) {
            stringBuilder.append(" ,success = ");
            stringBuilder.append(false);
        }
        stringBuilder.append(" ,args = ");
        stringBuilder.append(JSON.toJSONString(Arrays.toString(args)));

        if (response != null) {
            stringBuilder.append(" ,resp = ");
            stringBuilder.append(JSON.toJSONString(response));
        }

        if (exception != null) {
            stringBuilder.append(" ,exception = ");
            stringBuilder.append(exception.getMessage());
        }

        if (response instanceof BaseResponse baseResponse) {
            if (!baseResponse.getSuccess()) {
                stringBuilder.append(" , execute_failed");
            }
        }

        return stringBuilder.toString();
    }
}

