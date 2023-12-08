package com.neuma573.autoboard.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuma573.autoboard.security.filter.JwtTokenFilter;
import com.neuma573.autoboard.security.filter.VerifyUserFilter;
import com.neuma573.autoboard.security.service.TokenService;
import com.neuma573.autoboard.security.utils.JwtProvider;
import com.neuma573.autoboard.user.service.UserService;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean verifyUserFilterForFilter(ObjectMapper mapper, UserService userService) {
        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new VerifyUserFilter(mapper,userService));
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/api/v1/login");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean jwtFilter(JwtProvider provider, ObjectMapper mapper, UserService userService, TokenService tokenService) {
        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new JwtTokenFilter(provider, mapper, userService, tokenService));
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/api/v1/login");
        return filterRegistrationBean;
    }
}