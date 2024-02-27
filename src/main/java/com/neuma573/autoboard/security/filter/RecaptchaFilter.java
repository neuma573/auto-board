package com.neuma573.autoboard.security.filter;

import com.neuma573.autoboard.global.exception.ExceptionCode;
import com.neuma573.autoboard.global.exception.RecaptchaValidationException;

import com.neuma573.autoboard.global.model.dto.RecaptchaResponse;
import com.neuma573.autoboard.global.utils.RequestUtils;
import com.neuma573.autoboard.global.utils.ResponseUtils;
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
    private final ResponseUtils responseUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = RequestUtils.getRequestUri(httpServletRequest);
        HttpMethod method = RequestUtils.getMethod(httpServletRequest);
        try {
            if (urlPatternManager.isRecaptchaV3ProtectedUrl(requestURI, method) || urlPatternManager.isRecaptchaV2ProtectedUrl(requestURI, method)) {
                log.info("Recaptcha Verification Request URI : [{}] {}", method, requestURI);
                RecaptchaResponse recaptchaResponse = recaptchaService.createAssessment(httpServletRequest);
                if (!recaptchaResponse.isSuccess()) {
                    throw new RecaptchaValidationException("Recaptcha Validation Failed");
                }
            }
        } catch (RecaptchaValidationException ex) {
            responseUtils.setResponse(httpServletResponse, ExceptionCode.ILLEGAL_RECAPTCHA_REQUEST, ex);
            return;
        }


        filterChain.doFilter(httpServletRequest, httpServletResponse); // 검증 성공시 다음 필터로 요청 전달
    }
}
