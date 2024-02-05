package com.neuma573.autoboard.security.utils;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class UrlPatternManager {

    private static final Set<String> ALLOWED_PATHS = new HashSet<>();
    private static final Set<Pattern> ALLOWED_PATTERNS = new HashSet<>();

    static {
        Arrays.asList("/static/.*", "/images/.*", "/js/.*", "/css/.*", "/favicon.ico", "/ads.txt").forEach(
                pattern -> ALLOWED_PATTERNS.add(Pattern.compile(pattern))
        );

        ALLOWED_PATHS.addAll(Arrays.asList(
                "/api/v1/policy/tos",
                "/api/v1/post/permission",
                "/api/v1/users",
                "/api/v1/users/email-check",
                "/api/v1/auth/authenticate",
                "/api/v1/auth/verify",
                "/api/v1/auth/verify-token",
                "/api/v1/auth/refresh/token",
                "/api/v1/board",
                "/api/v1/post/list",
                "/api/v1/post",
                "/api/v1/comment/list",
                "/join",
                "/login",
                "/main",
                "/post",
                "/"
        ));
    }

    public boolean isProtectedUrl(String url) {
        return ALLOWED_PATHS.stream().noneMatch(url::equals) &&
                ALLOWED_PATTERNS.stream().noneMatch(pattern -> pattern.matcher(url).matches());
    }
}