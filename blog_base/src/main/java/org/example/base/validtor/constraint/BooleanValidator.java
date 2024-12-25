package org.example.base.validtor.constraint;


import org.example.base.validtor.annotion.BooleanNotNULL;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/**
 * @description:
 * @author: moki
 * @date: 2024/12/25 17:02
 * @param: 判断Boolean类型是否为空【校验器】
 * @return: 
 **/
public class BooleanValidator implements ConstraintValidator<BooleanNotNULL, Boolean> {

    @Override
    public void initialize(BooleanNotNULL constraintAnnotation) {

    }

    @Override
    public boolean isValid(Boolean value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return true;
    }
}
