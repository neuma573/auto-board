package com.neuma573.autoboard.global.exception;

public class AccessDeniedException extends RuntimeException{
    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException() {
    }
}
