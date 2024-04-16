package com.neuma573.autoboard.security.service;

import com.neuma573.autoboard.security.model.dto.AccessTokenRequest;
import com.neuma573.autoboard.security.utils.JwtProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtProvider jwtProvider;

    public boolean validateToken(AccessTokenRequest accessTokenRequest) {
        return jwtProvider.validateAccessTokenWithoutResponse(accessTokenRequest.getAccessToken());

    }
}