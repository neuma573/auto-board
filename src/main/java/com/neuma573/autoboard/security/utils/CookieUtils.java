package com.neuma573.autoboard.security.utils;

import com.neuma573.autoboard.global.utils.RequestUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtils {

    public static Cookie createCookie(String name, String value, int maxAge, boolean isSecure, boolean isHttpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setSecure(isSecure);
        cookie.setHttpOnly(isHttpOnly);
        return cookie;
    }

    public static void addCookie(HttpServletResponse httpServletResponse, Cookie cookie) {
        httpServletResponse.addCookie(cookie);
    }

    public static Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = RequestUtils.getCookies(request);
        return cookies == null ? Optional.empty() :
                Arrays.stream(cookies)
                        .filter(cookie -> cookieName.equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue);
    }


    public static void deleteCookie(HttpServletResponse httpServletResponse, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        httpServletResponse.addCookie(cookie);
    }
}
