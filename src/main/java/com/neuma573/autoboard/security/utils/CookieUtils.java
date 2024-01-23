package com.neuma573.autoboard.security.utils;

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

    public static void addCookie(HttpServletResponse response, Cookie cookie) {
        response.addCookie(cookie);
    }

    public static Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        return cookies == null ? Optional.empty() :
                Arrays.stream(cookies)
                        .filter(cookie -> cookieName.equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue);
    }


    public static void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
