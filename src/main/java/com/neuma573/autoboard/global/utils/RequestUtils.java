package com.neuma573.autoboard.global.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;

import java.util.Map;

public class RequestUtils {

    public static Map<String, String[]> getParameterMap(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getParameterMap();
    }

    public static String getClientIpAddress(HttpServletRequest httpServletRequest) {
        String xForwardedForHeader = httpServletRequest.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null) {
            return xForwardedForHeader.split(",")[0];
        }
        return httpServletRequest.getRemoteAddr();
    }

    public static String getUserAgent(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("User-Agent");
    }

    public static String getRequestUri(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getRequestURI();
    }

    public static Cookie[] getCookies(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getCookies();
    }

    public static String getHeader(HttpServletRequest httpServletRequest, String headerName) {
        return httpServletRequest.getHeader(headerName);
    }

    public static HttpMethod getMethod(HttpServletRequest httpServletRequest) {
        return HttpMethod.valueOf(httpServletRequest.getMethod());
    }
}
