package com.neuma573.autoboard.security.filter;

import com.neuma573.autoboard.security.utils.JwtProvider;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter implements Filter {

    private final JwtProvider jwtProvider;

    private static final Set<String> ALLOWED_PATHS = new HashSet<>();

    static {

        ALLOWED_PATHS.add("/api/v1/users");
        ALLOWED_PATHS.add("/api/v1/users/email-check");
        ALLOWED_PATHS.add("/api/v1/auth/authenticate");
        ALLOWED_PATHS.add("/api/v1/auth/refresh/token");
        ALLOWED_PATHS.add("/join");
        ALLOWED_PATHS.add("/login");
        ALLOWED_PATHS.add("/main");

        ALLOWED_PATHS.add("/");

        ALLOWED_PATHS.add("/static");
        ALLOWED_PATHS.add("/images");
        ALLOWED_PATHS.add("/js");
        ALLOWED_PATHS.add("/css");
        ALLOWED_PATHS.add("/favicon.ico");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String requestURI = httpRequest.getRequestURI();

        String httpMethod = httpRequest.getMethod();

        log.info("Request URI : [{}] {}", httpMethod, requestURI);

        if (isProtectedUrl(requestURI)) {
            String jwt = jwtProvider.parseJwtToken(httpRequest);
            if (jwt == null || !jwtProvider.validateAccessToken(jwt, httpResponse)) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or Missing JWT Token");
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }

    private boolean isProtectedUrl(String requestURI) {

        if (requestURI.startsWith("/static/") || requestURI.startsWith("/images/")
                || requestURI.startsWith("/js/") || requestURI.startsWith("/css/")) {
            return false;
        }

        return !ALLOWED_PATHS.contains(requestURI);
    }
}
