package com.neuma573.autoboard.user.controller;

import com.neuma573.autoboard.user.model.dto.GoogleUserResponse;
import com.neuma573.autoboard.user.model.dto.NaverUserResponse;
import com.neuma573.autoboard.user.model.enums.AuthenticationProviderType;
import com.neuma573.autoboard.user.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
@Slf4j
@RestController
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/naver/callback")
    public ResponseEntity<Void> getNaverOAuthCallback(
            @RequestParam(value = "code") String code,
            @RequestParam(value = "state") String state
    )
    {
        NaverUserResponse naverUserResponse = oAuthService.getAuthenticate(code, state);
        return handleLoginAndRedirect(
                naverUserResponse.getId(),
                naverUserResponse.getEmail(),
                AuthenticationProviderType.NAVER
        );
    }

    @GetMapping("/google/callback")
    public ResponseEntity<Void> getGoogleOAuthCallback(
            @RequestParam(value = "code") String code
    )
    {
        GoogleUserResponse googleUserResponse = oAuthService.getAuthenticate(code);
        return handleLoginAndRedirect(
                googleUserResponse.getId(),
                googleUserResponse.getEmail(),
                AuthenticationProviderType.GOOGLE
        );
    }

    private ResponseEntity<Void> handleLoginAndRedirect(
            String providerId,
            String email,
            AuthenticationProviderType authenticationProviderType
    )
    {
        String joinUuid = oAuthService.handleOAuthLogin(providerId, email, authenticationProviderType);
        if (!joinUuid.isEmpty()) {
            String encodedUuid = URLEncoder.encode(joinUuid, StandardCharsets.UTF_8);
            URI joinUri = URI.create("/oauth/join?code=" + encodedUuid);
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(joinUri).build();
        } else {
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create("/main")).build();
        }
    }
}

