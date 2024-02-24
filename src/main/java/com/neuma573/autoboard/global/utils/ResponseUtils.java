package com.neuma573.autoboard.global.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuma573.autoboard.global.exception.ExceptionCode;
import com.neuma573.autoboard.global.model.dto.Response;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResponseUtils {

    private final ObjectMapper objectMapper;

    public void setResponse(HttpServletResponse httpServletResponse, ExceptionCode exceptionCode, Exception ex) throws IOException {
        log.error("Exception : {}, Message : {}", ex.getClass().getSimpleName(), ex.getMessage());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setStatus(exceptionCode.getStatus().value());

        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(
                Response.builder()
                .status(exceptionCode.getStatus().value())
                .message(exceptionCode.getMessage())
                .build()
        ));
    }

    public <T> Response<T> success(T data) {
        return Response.<T>builder()
                .status(HttpStatus.OK.value())
                .data(data)
                .build();
    }

    public <T> Response<T> created(T data) {
        return Response.<T>builder()
                .status(HttpStatus.CREATED.value())
                .data(data)
                .build();
    }

    public Response<String> error(ExceptionCode status, Exception e) {
        return Response.<String>builder()
                .status(status.getStatus().value())
                .message(status.getMessage())
                .data(e.getClass().getSimpleName())
                .build();
    }

}
