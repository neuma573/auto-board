package com.neuma573.autoboard.global.exception;

public class RecaptchaValidationException extends RuntimeException {
    public RecaptchaValidationException(String message) {
        super(message);
    }
}
