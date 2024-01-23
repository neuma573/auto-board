package com.neuma573.autoboard.global.handler;

import com.neuma573.autoboard.global.exception.*;
import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.security.utils.CookieUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

import static com.neuma573.autoboard.global.exception.ExceptionCode.*;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseUtils responseUtils;

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<String>> handleAccessDeniedException(AccessDeniedException ex) {
        log.info(ex.getMessage());
        Response<String> response = responseUtils.error(NOT_ENOUGH_ROLE, ex);
        return new ResponseEntity<>(response, NOT_ENOUGH_ROLE.getStatus());
    }

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.info(ex.getMessage());
        String errorMessages = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        Response<String> response = Response.<String>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .data(ex.getClass().getSimpleName())
                .message(errorMessages)
                .build();
        return new ResponseEntity<>(response, BAD_REQUEST.getStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response<String>> handleMethodArgumentNotValidException(DataIntegrityViolationException ex) {
        log.info(ex.getMessage());
        Response<String> response = responseUtils.error(BAD_REQUEST, ex);
        return new ResponseEntity<>(response, BAD_REQUEST.getStatus());
    }

    @ExceptionHandler(NotActivatedUserException.class)
    public ResponseEntity<Response<String>> handleNotActivatedUserException(NotActivatedUserException ex){
        log.info(ex.getMessage());
        Response<String> response = responseUtils.error(NOT_ACTIVATED_USER, ex);
        return new ResponseEntity<>(response, NOT_ACTIVATED_USER.getStatus());
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<Response<String>> handleTokenNotFoundException(TokenNotFoundException ex, HttpServletResponse httpServletResponse){
        log.info(ex.getMessage());
        CookieUtils.deleteCookie(httpServletResponse, "uuid");
        Response<String> response = responseUtils.error(BAD_REQUEST, ex);
        return new ResponseEntity<>(response, BAD_REQUEST.getStatus());
    }

    @ExceptionHandler({JwtException.class, ExpiredJwtException.class})
    public Object handleJwtException(JwtException ex, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.info(ex.getMessage());
        CookieUtils.deleteCookie(httpServletResponse, "uuid");
        if (isApiRequest(httpServletRequest)) {
            Response<String> response = responseUtils.error(UNAUTHORIZED, ex);
            return new ResponseEntity<>(response, UNAUTHORIZED.getStatus());
        } else {
            ModelAndView modelAndView = new ModelAndView("error/error");

            modelAndView.addObject("code", UNAUTHORIZED);
            modelAndView.addObject("message", ex.getMessage());
            return modelAndView;
        }
    }


    @ExceptionHandler(Exception.class)
    public Object handleException(Exception ex, HttpServletRequest httpServletRequest) {

        ex.printStackTrace();

        if (isApiRequest(httpServletRequest)) {
            Response<String> response = responseUtils.error(INTERNAL_SERVER_ERROR, ex);
            return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR.getStatus());
        } else {
            ModelAndView modelAndView = new ModelAndView("error/error");
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            Object statusObject = httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            if (statusObject != null) {
                int statusCode = Integer.parseInt(statusObject.toString());
                status = HttpStatus.valueOf(statusCode);
            }

            modelAndView.addObject("code", status.value());
            modelAndView.addObject("message", status.getReasonPhrase());
            return modelAndView;
        }

    }

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api");
    }
}
