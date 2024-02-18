package com.neuma573.autoboard.global.exception;

public class UserBlockedException extends RuntimeException {
    public UserBlockedException() {
        super();
    }

    public UserBlockedException(String message) {
        super(message);
    }
}