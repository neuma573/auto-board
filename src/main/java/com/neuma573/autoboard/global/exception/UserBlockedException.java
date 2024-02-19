package com.neuma573.autoboard.global.exception;

public class UserBlockedException extends RuntimeException {
    public UserBlockedException() {
        super();
    }

    public UserBlockedException(String message) {
        super(message);
    }

    public UserBlockedException(Long userId) {
        super("Banned user [{}] tried illegal action".replace("{}", String.valueOf(userId)));
    }
}