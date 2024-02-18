package com.neuma573.autoboard.global.exception;

public class UserBlockedException extends RuntimeException {
    public UserBlockedException() {
        super("차단되었습니다. 관리자에게 문의하세요 : autoboardsite@gmail.com");
    }

    public UserBlockedException(String message) {
        super(message);
    }
}