package com.neuma573.autoboard.global.handler;

import com.neuma573.autoboard.global.exception.ExceptionCode;
import com.neuma573.autoboard.global.model.dto.CustomErrorResponse;
import com.neuma573.autoboard.global.model.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAnyException(Exception ex, WebRequest request) {
        log.error("Exception : {}, Message : {}",ex.getClass(), ex.getMessage());
        log.error("Request URL: {}", request.getDescription(false));



        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());

    }

}
