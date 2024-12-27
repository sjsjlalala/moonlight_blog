package org.example.base.validtor.constraint;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.example.base.validtor.annotion.Numeric;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/**
 * @description:
 * @author: moki
 * @date: 2024/12/25 17:01
 * @param: 判断是否为数字【校验器】
 * @return: 
 **/
public class NumericValidator implements ConstraintValidator<Numeric, String> {
    @Override
    public void initialize(Numeric constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || StringUtils.isBlank(value)) {
            return false;
        }
        if (!StrUtil.isNumeric(value)) {
            return false;
        }
        return true;
    }
}
