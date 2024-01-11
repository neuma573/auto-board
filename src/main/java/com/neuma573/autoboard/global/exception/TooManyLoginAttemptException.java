package com.neuma573.autoboard.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Access Denied")
public class TooManyLoginAttemptException extends RuntimeException {

    public TooManyLoginAttemptException() {
        super();
    }

    public TooManyLoginAttemptException(String message) {
        super(message);
    }

    public TooManyLoginAttemptException(String message, Throwable cause) {
        super(message, cause);
    }
}