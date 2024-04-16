package com.neuma573.autoboard.security.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecaptchaRequest {

    private String recaptchaToken;

    private String recaptchaAction;

    private String recaptchaVersion;

}
