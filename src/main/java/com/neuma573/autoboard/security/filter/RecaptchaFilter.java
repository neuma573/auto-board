package com.neuma573.autoboard.security.filter;

import com.neuma573.autoboard.global.utils.RequestUtils;
import com.neuma573.autoboard.security.service.RecaptchaService;
import com.neuma573.autoboard.security.utils.UrlPatternManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class RecaptchaFilter extends OncePerRequestFilter {

    private final RecaptchaService recaptchaService;
    private final UrlPatternManager urlPatternManager;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = RequestUtils.getRequestUri(httpServletRequest);
        HttpMethod method = RequestUtils.getMethod(httpServletRequest);
        if (urlPatternManager.isRecaptchaProtectedUrl(requestURI, method)) {
            log.info("Recaptcha Verification Request URI : [{}] {}", method, requestURI);
            if (!recaptchaService.createAssessment(httpServletRequest)) {
                httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid reCAPTCHA token");
                return; // 리캡차 검증 실패시 요청 처리 중단
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse); // 검증 성공시 다음 필터로 요청 전달
    }
}
