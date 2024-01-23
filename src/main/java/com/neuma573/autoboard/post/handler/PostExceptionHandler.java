package com.neuma573.autoboard.post.handler;

import com.neuma573.autoboard.global.exception.PostNotAccessibleException;
import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.neuma573.autoboard.global.exception.ExceptionCode.UNAUTHORIZED;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class PostExceptionHandler {


    private final ResponseUtils responseUtils;
    @ExceptionHandler(PostNotAccessibleException.class)
    public ResponseEntity<Response<String>> handlePostNotAccessibleException(PostNotAccessibleException ex) {
        log.info(ex.getMessage());
        Response<String> response = responseUtils.error(UNAUTHORIZED, ex);
        return new ResponseEntity<>(response, UNAUTHORIZED.getStatus());
    }

}
