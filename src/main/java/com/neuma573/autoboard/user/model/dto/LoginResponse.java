package com.neuma573.autoboard.user.model.dto;

import com.nimbusds.jwt.JWT;
import io.jsonwebtoken.Jwt;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private String refreshToken;

    private String accessToken;
}
