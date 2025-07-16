package com.urban.carbon.base.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 手机号验证注解，通过该注解对手机号验证的方法进行封装
 *
 * <p>使用方法示例：</p>
 * <ul>
 *     <li>在实体类字段上使用：
 *         <pre>{@code @IsMobile private String phone;}</pre>
 *     </li>
 *     <li>在 Controller 接口参数中使用：
 *         <pre>{@code @PostMapping("/users") public void createUser(@Valid @RequestBody UserRequest request) { ... }}</pre>
 *     </li>
 *     <li>结合自定义错误提示信息使用：
 *         <pre>{@code @IsMobile(message = "请输入正确的手机号") private String contactNumber;}</pre>
 *     </li>
 * </ul>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Constraint(validatedBy = MobileValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsMobile {

    /**
     * 错误信息
     *
     * @return 错误信息
     */
    String message() default "手机号格式不正确"; // 默认错误信息

    /**
     * 分组
     *
     * @return 分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     *
     * @return 负载
     */
    Class<? extends Payload>[] payload() default {};
}

