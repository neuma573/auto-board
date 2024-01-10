package com.neuma573.autoboard.global.handler;

import com.neuma573.autoboard.global.exception.InvalidLoginException;
import com.neuma573.autoboard.global.exception.TooManyLoginAttemptException;
import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.neuma573.autoboard.global.exception.ExceptionCode.*;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseUtils responseUtils;

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Response<String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.info(ex.getMessage());
        Response<String> response = responseUtils.error(ENTITY_NOT_FOUND, ex);
        return new ResponseEntity<>(response, ENTITY_NOT_FOUND.getStatus());
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<Response<String>> handleAuthenticationException(InvalidLoginException ex) {
        log.info(ex.getMessage());
        Response<String> response = responseUtils.error(INVALID_LOGIN, ex);
        return new ResponseEntity<>(response, INVALID_LOGIN.getStatus());
    }

    @ExceptionHandler(TooManyLoginAttemptException.class)
    public ResponseEntity<Response<String>> handleTooManyAttemptException(TooManyLoginAttemptException ex) {
        log.info(ex.getMessage());
        Response<String> response = responseUtils.error(TOO_MANY_LOGIN_ATTEMPT, ex);
        return new ResponseEntity<>(response, TOO_MANY_LOGIN_ATTEMPT.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<String>> handleException(Exception ex) {
        log.info(ex.getMessage());
        Response<String> response = responseUtils.error(INTERNAL_SERVER_ERROR, ex);
        return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR.getStatus());
    }
}
