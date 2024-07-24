package com.neuma573.autoboard.security.utils;

import com.neuma573.autoboard.security.model.dto.UrlPattern;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class UrlPatternManager {

    private static final Set<String> ALLOWED_PATHS = new HashSet<>();
    private static final Set<Pattern> ALLOWED_PATTERNS = new HashSet<>();

    private static final Set<UrlPattern> RECAPTCHA_V2_VALIDATION_PATTERNS = new HashSet<>();

    private static final Set<UrlPattern> RECAPTCHA_V3_VALIDATION_PATTERNS = new HashSet<>();

    static {
        Arrays.asList("/static/.*", "/images/.*", "/js/.*", "/css/.*", "/favicon.ico", "/ads.txt", "/api/v1/file/.*",  "/policy/.*", "/robots.txt").forEach(
                pattern -> ALLOWED_PATTERNS.add(Pattern.compile(pattern))
        );

        ALLOWED_PATHS.addAll(Arrays.asList(
                "/api/v1/comment/replies",
                "/api/v1/oauth2/naver/callback",
                "/api/v1/oauth2/google/callback",
                "/api/v1/oauth2/user",
                "/api/v1/policy/tos",
                "/api/v1/post/permission",
                "/api/v1/users",
                "/api/v1/users/email-check",
                "/api/v1/auth/authenticate",
                "/api/v1/auth/verify-token",
                "/api/v1/auth/refresh/token",
                "/api/v1/board",
                "/api/v1/post/list",
                "/api/v1/post",
                "/api/v1/comment/list",
                "/auth/verify",
                "/auth/oauth",
                "/signup-options",
                "/oauth/join",
                "/join",
                "/login",
                "/main",
                "/post",
                "/"
        ));

        RECAPTCHA_V2_VALIDATION_PATTERNS.add(UrlPattern.of("/api/v1/post", Set.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)));
        RECAPTCHA_V2_VALIDATION_PATTERNS.add(UrlPattern.of("/api/v1/users", Set.of(HttpMethod.POST)));
        RECAPTCHA_V2_VALIDATION_PATTERNS.add(UrlPattern.of("/api/v1/auth/authenticate", Set.of(HttpMethod.POST)));


        RECAPTCHA_V3_VALIDATION_PATTERNS.add(UrlPattern.of("/api/v1/users/email-check", Set.of(HttpMethod.GET)));
        RECAPTCHA_V3_VALIDATION_PATTERNS.add(UrlPattern.of("/api/v1/post", Set.of(HttpMethod.DELETE)));
        RECAPTCHA_V3_VALIDATION_PATTERNS.add(UrlPattern.of("/api/v1/comment", Set.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)));

    }

    public boolean isProtectedUrl(String url) {
        return ALLOWED_PATHS.stream().noneMatch(url::equals) &&
                ALLOWED_PATTERNS.stream().noneMatch(pattern -> pattern.matcher(url).matches());
    }

    public boolean isRecaptchaV2ProtectedUrl(String url, HttpMethod method) {
        return RECAPTCHA_V2_VALIDATION_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matchesUrl(url) && pattern.matchesMethod(method));
    }

    public boolean isRecaptchaV3ProtectedUrl(String url, HttpMethod method) {
        return RECAPTCHA_V3_VALIDATION_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matchesUrl(url) && pattern.matchesMethod(method));
    }
}