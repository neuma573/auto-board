package com.neuma573.autoboard.global.exception;

public class NotActivatedUserException extends RuntimeException{
    public NotActivatedUserException() {
        super();
    }

    public NotActivatedUserException(String message) {
        super(message);
    }

    public NotActivatedUserException(String message, Throwable cause) {
        super(message, cause);
    }

}
