package com.neuma573.autoboard.security.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AccessTokenResponse {

    private String accessToken;

}
