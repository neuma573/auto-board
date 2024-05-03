package com.neuma573.autoboard.post.validation.validator;

import com.neuma573.autoboard.global.utils.ContentSanitizer;
import com.neuma573.autoboard.post.validation.annotation.ValidContent;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidContentValidator implements ConstraintValidator<ValidContent, String> {
    @Override
    public void initialize(ValidContent constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        String modifiedValue = value.replace("\u00AD", "");
        modifiedValue = ContentSanitizer.removeHtmlTags(modifiedValue);
        return !modifiedValue.trim().isEmpty();
    }
}
