package com.neuma573.autoboard.user.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GoogleTokenResponse extends TokenResponse {
    @JsonProperty("scope")
    private String scope;
}
