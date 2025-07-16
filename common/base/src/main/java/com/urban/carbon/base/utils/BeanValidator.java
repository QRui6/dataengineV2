package com.urban.carbon.base.utils;

import jakarta.validation.*;
import org.hibernate.validator.HibernateValidator;

import java.util.Set;

/**
 * Bean参数校验工具类。
 *
 * <p>该类封装了基于 Hibernate Validator 的参数校验逻辑，
 * 支持快速失败模式（failFast），即发现第一个错误即停止校验。</p>
 *
 * <p><strong>使用方式示例：</strong></p>
 * <pre>{@code
 * UserRequest request = new UserRequest();
 * BeanValidator.validateObject(request);
 * }</pre>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
public class BeanValidator {

    /**
     * 使用 Hibernate Validator 构建的 Validator 实例，
     * 配置为 failFast 模式，即遇到第一个校验失败即返回。
     */
    private static final Validator validator = buildValidator();

    /**
     * 构建并返回一个 Validator 实例。
     *
     * <p>该方法使用 try-with-resources 确保 ValidatorFactory 被正确关闭，
     * 避免资源泄漏。构建的 Validator 实例用于执行 Bean Validation 校验，
     * 并启用 failFast 模式，即遇到第一个校验错误时立即停止。</p>
     *
     * @return 返回配置好的 Validator 实例
     * @since 0.0.2
     */
    private static Validator buildValidator() {
        try (ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(true)
                .buildValidatorFactory()) {
            return factory.getValidator();
        }
    }


    /**
     * 校验目标对象是否符合约束条件。
     *
     * <p>如果校验失败，会抛出 {@link ValidationException}，
     * 异常信息包含具体的校验失败原因。</p>
     *
     * @param obj    要校验的对象
     * @param groups 可选的校验分组
     * @throws ValidationException 当校验失败时抛出
     */
    public static void validateObject(Object obj, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj, groups);
        if (constraintViolations.stream().findFirst().isPresent()) {
            throw new ValidationException(constraintViolations.stream().findFirst().get().getMessage());
        }
    }
}

