package com.urban.carbon.web.handler;

import cn.dev33.satoken.exception.SaTokenException;
import com.google.common.collect.Maps;
import com.urban.carbon.base.exception.BizException;
import com.urban.carbon.base.exception.SystemException;
import com.urban.carbon.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

import static com.urban.carbon.base.response.ResponseCode.SYSTEM_ERROR;

/**
 * 定义一个全局的 controller 异常捕获机制,
 * 用来捕获 controller 可能发生的错误
 *
 * @author XuGaoran
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 当 Controller 发现当前传入的请求参数都存在问题的时候,
     * 将调用该拦截器对异常进行处理
     *
     * @param ex 被抛出的 Method Args 异常
     * @return 返回被包装的参数
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> methodArgumentNotValidHandler(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException occurred.", ex);
        Map<String, String> errors = Maps.newHashMapWithExpectedSize(1);
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * 自定义业务异常的处理方法
     *
     * @param bizException 捕捉到的业务异常
     * @return 封装好的结果
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> BizExceptionHandler(BizException bizException) {
        log.error("biz Exception occurred.", bizException);
        String errorMsg;
        if (bizException.getErrorCode() != null) {
            errorMsg = bizException.getErrorCode().getMessage();
        } else {
            errorMsg = bizException.getMessage();
        }
        return new Result<>(false, bizException.getErrorCode().getCode(), errorMsg, null);
    }

    /**
     * SaToken 异常处理方法
     *
     * @param exception 被抛出的 SaToken 异常
     * @return 封装好的结果
     */
    @ExceptionHandler(SaTokenException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> SaTokenExceptionHandler(Exception exception) {
        log.error("Sa Token Exception occurred.", exception);
        return new Result<>(false, SYSTEM_ERROR.name(),
                "请求失败，请先检查登录状态~", null);
    }

    /**
     * 自定义业务异常的处理方法
     *
     * @param systemException 捕捉到的业务异常
     * @return 封装好的结果
     */
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> systemExceptionHandler(SystemException systemException) {
        log.error("System Exception occurred.", systemException);
        String errorMsg;
        if (systemException.getErrorCode() != null) {
            errorMsg = systemException.getErrorCode().getMessage();
        } else {
            errorMsg = systemException.getMessage();
        }
        return new Result<>(false, systemException.getErrorCode().getCode(),
                errorMsg, null);
    }

    /**
     * 通用异常处理方法
     *
     * @param throwable 被抛出的 Throwable 异常
     * @return 封装好的结果
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> throwableHandler(Throwable throwable) {
        log.error("throwable occurred.", throwable);
        return new Result<>(false, SYSTEM_ERROR.name(),
                "~当前网络比较拥挤, 请您稍后再试", null);
    }
}

