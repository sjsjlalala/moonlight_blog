package org.example.base.validtor.annotion;


import org.example.base.enums.Messages;
import org.example.base.validtor.constraint.BooleanValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * @description:
 * @author: moki
 * @date: 2024/12/25 17:03
 * @param: 判断Boolean类型是否为空【注解】
 * @return: 
 **/
@Target({TYPE, ANNOTATION_TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BooleanValidator.class})
public @interface BooleanNotNULL {

    boolean required() default true;

    String message() default Messages.CK_NOT_NULL_DEFAULT;

    String value() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
