package com.neuma573.autoboard.security.filter;

import com.neuma573.autoboard.security.utils.JwtProvider;
import com.neuma573.autoboard.security.utils.UrlPatternManager;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter implements Filter {

    private final JwtProvider jwtProvider;

    private final UrlPatternManager urlPatternManager;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String requestURI = httpRequest.getRequestURI();

        log.info("Request URI : [{}] {}", httpRequest.getMethod(), requestURI);

        if (urlPatternManager.isProtectedUrl(requestURI)) {
            String jwt = jwtProvider.parseJwtToken(httpRequest);
            if (jwt == null || !jwtProvider.validateAccessToken(jwt, httpResponse)) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or Missing JWT Token");
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }
}
