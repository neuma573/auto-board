package com.neuma573.autoboard.user.validation.validator;

import com.neuma573.autoboard.user.validation.annotation.ValidEmailDomain;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class EmailDomainValidator implements ConstraintValidator<ValidEmailDomain, String> {

    private List<String> allowedDomains;

    @Override
    public void initialize(ValidEmailDomain constraintAnnotation) {
        allowedDomains = Arrays.asList(constraintAnnotation.allowedDomains());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        String domain = value.substring(value.lastIndexOf("@") + 1);
        return allowedDomains.contains(domain);
    }
}
