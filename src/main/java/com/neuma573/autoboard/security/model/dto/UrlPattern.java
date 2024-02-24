package com.neuma573.autoboard.security.model.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.Set;
import java.util.regex.Pattern;

@Builder
@Getter
public class UrlPattern {
    private String pattern;
    private Set<HttpMethod> methods;

    public boolean matchesMethod(HttpMethod method) {
        return methods.isEmpty() || methods.contains(method);
    }

    public boolean matchesUrl(String url) {
        return Pattern.matches(pattern, url);
    }

    public static UrlPattern of(String pattern, Set<HttpMethod> methods) {
        return UrlPattern.builder()
                .pattern(pattern)
                .methods(methods)
                .build();
    }
}
