package com.neuma573.autoboard.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RecaptchaService {

    @Value("${app.recaptcha.secret}")
    private String recaptchaSecret;

    @Value("${app.recaptcha.url}")
    private String recaptchaUrl;


}
