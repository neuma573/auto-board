package com.neuma573.autoboard.post.validation.annotation;

import com.neuma573.autoboard.post.validation.validator.ValidContentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidContentValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidContent {
    String message() default "must not be blank or empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
