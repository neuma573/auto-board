package com.neuma573.autoboard.global.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecaptchaResponse {
    private boolean success;
    private float score;
}
