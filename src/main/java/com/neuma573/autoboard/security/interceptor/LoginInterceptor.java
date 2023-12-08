package com.neuma573.autoboard.security.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        boolean isAuthenticated = true;
        if (!isAuthenticated) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증되지 않은 사용자입니다.");
            return false; // 요청 처리 중지
        }

        return true; // 요청을 컨트롤러로 계속 전달
    }

    // postHandle과 afterCompletion은 필요에 따라 구현
}