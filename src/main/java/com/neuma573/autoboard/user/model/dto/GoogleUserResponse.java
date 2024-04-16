package com.neuma573.autoboard.user.model.dto;

import lombok.Getter;

@Getter
public class GoogleUserResponse {
    private String id;
    private String email;
    private boolean verifiedEmail;
    private String picture;
}
