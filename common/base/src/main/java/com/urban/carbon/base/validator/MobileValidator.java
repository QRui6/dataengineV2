package com.urban.carbon.base.validator;

import cn.hutool.core.lang.Validator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 手机号验证的具体实现方法, 本质是实现 jakarta 中的验证方法
 *
 * @author XuGaoran
 * @since 0.0.1
 */
public class MobileValidator implements ConstraintValidator<IsMobile, String> {

    /**
     * 判断value数值是否符合手机号的规范
     *
     * @param value 需要验证的手机号
     * @param context 上下文
     * @return 返回手机号是否合理
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Validator.isMobile(value);
    }
}

