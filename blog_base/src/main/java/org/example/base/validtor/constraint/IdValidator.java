package org.example.base.validtor.constraint;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.example.base.constants.Constants;
import org.example.base.validtor.annotion.IdValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @description:
 * @author: moki
 * @date: 2024/12/25 17:02
 * @param: ID校验器，主要判断是否为空，并且长度是否为32
 * @return: 
 **/
public class IdValidator implements ConstraintValidator<IdValid, String> {


    @Override
    public void initialize(IdValid constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || StringUtils.isBlank(value) || org.springframework.util.StringUtils.isEmpty(value.trim()) || value.length() != Constants.THIRTY_TWO) {
            return false;
        }
        return true;
    }
}
