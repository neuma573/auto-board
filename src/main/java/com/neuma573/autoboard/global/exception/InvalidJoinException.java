package com.neuma573.autoboard.global.exception;

public class InvalidJoinException extends RuntimeException {

    public InvalidJoinException() {
        super();
    }

    public InvalidJoinException(String message) {
        super(message);
    }

    public InvalidJoinException(String message, Throwable cause) {
        super(message, cause);
    }

}
