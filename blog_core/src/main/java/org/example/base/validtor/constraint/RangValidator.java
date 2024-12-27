package org.example.base.validtor.constraint;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.example.base.validtor.annotion.Range;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @description:
 * @author: moki
 * @date: 2024/12/25 17:02
 * @param: 符串范围约束，限制长度【校验器】
 * @return: 
 **/
public class RangValidator implements ConstraintValidator<Range, String> {
    private long min;
    private long max;

    @Override
    public void initialize(Range constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (null == value || StringUtils.isBlank(value)) {
            return min == 0;
        }
        return value.length() >= min && value.length() <= max;
    }
}
