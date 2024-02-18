package com.neuma573.autoboard.global.config;

import com.neuma573.autoboard.global.interceptor.RateLimitingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitingInterceptor rateLimitingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitingInterceptor)
                .addPathPatterns("/api/v1/post")
                .addPathPatterns("/api/v1/comment")
                .addPathPatterns("/api/v1/users")
                .addPathPatterns("/api/v1/auth/authenticate");
    }
}
