package com.neuma573.autoboard.security.filter;

import com.neuma573.autoboard.global.utils.RequestUtils;
import com.neuma573.autoboard.security.utils.CookieUtils;
import com.neuma573.autoboard.security.utils.JwtProvider;
import com.neuma573.autoboard.security.utils.UrlPatternManager;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

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
        log.info("[{}] : [{}] {}", RequestUtils.getClientIpAddress(httpRequest) , httpRequest.getMethod(), requestURI);

        if (urlPatternManager.isProtectedUrl(requestURI) && !isAuthorizedRequest(httpRequest, httpResponse)) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or Missing JWT Token");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isAuthorizedRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        Optional<String> accessTokenOpt = jwtProvider.parseJwtToken(httpServletRequest);

        boolean isMvcRequest = !RequestUtils.getRequestUri(httpServletRequest).startsWith("/api");

        if (CookieUtils.getCookieValue(httpServletRequest, "uuid").isPresent() && (isMvcRequest || accessTokenOpt.map(token -> !jwtProvider.validateAccessTokenWithoutResponse(token)).orElse(true))) {
            jwtProvider.refreshAccessToken(httpServletRequest, httpServletResponse);
        }

        return accessTokenOpt.isPresent() && jwtProvider.validateAccessToken(accessTokenOpt.get(), httpServletResponse);
    }

}
