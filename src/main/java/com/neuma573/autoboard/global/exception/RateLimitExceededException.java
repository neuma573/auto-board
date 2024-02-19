package com.neuma573.autoboard.global.exception;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException() {
        super("너무 많은 요청을 할 수 없습니다");
    }

    public RateLimitExceededException(String message) {
        super(message);
    }

    public RateLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
