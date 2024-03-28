package com.neuma573.autoboard.user.service;

import com.neuma573.autoboard.global.client.GoogleAuthClient;
import com.neuma573.autoboard.global.client.GoogleUserClient;
import com.neuma573.autoboard.global.client.NaverAuthClient;
import com.neuma573.autoboard.global.client.NaverUserClient;
import com.neuma573.autoboard.user.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuthService {

    private final NaverAuthClient naverAuthClient;

    private final NaverUserClient naverUserClient;

    private final GoogleAuthClient googleAuthClient;

    private final GoogleUserClient googleUserClient;

    @Value("${app.oauth2.naver.client-id}")
    private String naverClientId;

    @Value("${app.oauth2.naver.client-secret}")
    private String naverClientSecret;

    @Value("${app.oauth2.google.client-id}")
    private String googleClientId;

    @Value("${app.oauth2.google.client-secret}")
    private String googleClientSecret;

    @Value("${app.domain}")
    private String domain;

    public NaverUserResponse getAuthenticate(String code, String state) {
        NaverTokenResponse tokenResponse = getToken(code, state);
        return getNaverUserResponse(tokenResponse);
    }

    public GoogleUserResponse getAuthenticate(String code) {
        GoogleTokenResponse tokenResponse = getToken(code);
        return getGoogleUserResponse(tokenResponse);
    }

    private NaverTokenResponse getToken(String code, String state) {
        return naverAuthClient.getToken(
                "authorization_code",
                naverClientId,
                naverClientSecret,
                code,
                state
        );
    }

    private GoogleTokenResponse getToken(String code) {
        return googleAuthClient.getToken(
                code,
                googleClientId,
                googleClientSecret,
                domain + "/api/v1/oauth2/google/callback",
                "authorization_code"
        );
    }

    private NaverUserResponse getNaverUserResponse(NaverTokenResponse tokenResponse) {
        return naverUserClient.getNaverUserResponse(getAuthorizationHeader(tokenResponse));
    }

    private GoogleUserResponse getGoogleUserResponse(GoogleTokenResponse tokenResponse) {
        return googleUserClient.getGoogleUserResponse(getAuthorizationHeader(tokenResponse));
    }

    private String getAuthorizationHeader(TokenResponse tokenResponse) {
        return tokenResponse.getTokenType() + " " + tokenResponse.getAccessToken();
    }
}
