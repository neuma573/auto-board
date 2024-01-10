package com.neuma573.autoboard.global.exception;

public class InvalidLoginException extends RuntimeException {

    public InvalidLoginException() {
        super();
    }

    public InvalidLoginException(String message) {
        super(message);
    }

    public InvalidLoginException(String message, Throwable cause) {
        super(message, cause);
    }

}
