package com.neuma573.autoboard.board.handler;

import com.neuma573.autoboard.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class BoardExceptionHandler {

    private final ResponseUtils responseUtils;
}
