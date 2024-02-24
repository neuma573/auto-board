package com.neuma573.autoboard.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "대상이 존재하지 않습니다"),

    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "JWT 토큰이 유효하지 않습니다"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "JWT 토큰이 만료되었습니다"),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED,"지원하지 않는 토큰입니다"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "리프레시 토큰시간이 만료되었습니다. 다시 로그인 해주세요"),
    NOT_FOUND_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 존재하지 않습니다"),
    INVALID_JWT_ISSUER(HttpStatus.FORBIDDEN, "토큰의 서명이 올바르지 않습니다"),
    INVALID_JWT_TYPE(HttpStatus.FORBIDDEN, "토큰의 타입이 올바르지 않습니다"),

    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "아이디나 비밀번호가 틀렸습니다"),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다"),

    TOO_MANY_LOGIN_ATTEMPT(HttpStatus.FORBIDDEN, "너무 많은 로그인 시도를 하여 계정이 잠겼습니다. 잠시 후 시도해주세요"),

    NOT_ENOUGH_ROLE(HttpStatus.FORBIDDEN, "권한이 없습니다"),
    INVALID_EMAIL_FORM(HttpStatus.BAD_REQUEST, "이메일 형식이 잘못됐습니다."),
    NOT_ACTIVATED_USER(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않은 사용자입니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다"),
    DUPLICATED_NAME(HttpStatus.BAD_REQUEST, "이미 사용중인 이름입니다"),
    TOO_MANY_ATTEMP(HttpStatus.TOO_MANY_REQUESTS, "너무 많이 요청할 수 없습니다"),
    BANNED_USER(HttpStatus.FORBIDDEN, "차단되었습니다. 관리자에게 문의하세요 : autoboardsite@gmail.com"),
    ILLEGAL_RECAPTCHA_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 reCaptcha 요청입니다.");


    private final HttpStatus status;
    private final String message;

}
