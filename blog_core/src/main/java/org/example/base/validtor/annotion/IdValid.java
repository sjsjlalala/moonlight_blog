package org.example.base.validtor.annotion;



import org.example.base.enums.Messages;
import org.example.base.validtor.constraint.IdValidator;

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
 * @param: D校验【注解】
 * @return: 
 **/
@Target({TYPE, ANNOTATION_TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {IdValidator.class})
public @interface IdValid {

    boolean required() default true;

    String message() default Messages.ID_LENGTH_THIRTY_TWO;

    String value() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
