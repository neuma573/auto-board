package com.neuma573.autoboard.board.handler;

import com.neuma573.autoboard.global.exception.BoardNotAccessibleException;
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
public class BoardExceptionHandler {

    private final ResponseUtils responseUtils;
    @ExceptionHandler(BoardNotAccessibleException.class)
    public ResponseEntity<Response<String>> handleBoardNotAccessibleException(BoardNotAccessibleException ex) {
        log.info(ex.getMessage());
        Response<String> response = responseUtils.error(UNAUTHORIZED, ex);
        return new ResponseEntity<>(response, UNAUTHORIZED.getStatus());
    }
}
