package com.neuma573.autoboard.post.handler;

import com.neuma573.autoboard.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
@ControllerAdvice
public class PostExceptionHandler {


    private final ResponseUtils responseUtils;


}
