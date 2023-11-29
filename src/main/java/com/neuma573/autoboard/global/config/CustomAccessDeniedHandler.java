package com.neuma573.autoboard.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuma573.autoboard.global.exception.ExceptionCode;
import com.neuma573.autoboard.global.model.dto.Response;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        setResponse(response);
    }

    private void setResponse(HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Response<Void> fail = Response.error(ExceptionCode.NOT_ENOUGH_ROLE.getStatus().toString(), ExceptionCode.NOT_ENOUGH_ROLE.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(fail));
    }
}
