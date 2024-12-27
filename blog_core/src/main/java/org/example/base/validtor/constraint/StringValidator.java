package org.example.base.validtor.constraint;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.example.base.validtor.annotion.NotBlank;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @description:
 * @author: moki
 * @date: 2024/12/25 17:03
 * @param: 判断是否为空字符串【校验器】
 * @return: 
 **/
public class StringValidator implements ConstraintValidator<NotBlank, String> {
    @Override
    public void initialize(NotBlank constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || StringUtils.isBlank(value) || org.springframework.util.StringUtils.isEmpty(value.trim())) {
            return false;
        }
        return true;
    }
}
