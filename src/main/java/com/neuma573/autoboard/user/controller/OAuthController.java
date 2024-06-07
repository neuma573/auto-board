package com.neuma573.autoboard.user.controller;

import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.security.model.dto.AccessTokenResponse;
import com.neuma573.autoboard.security.model.dto.ClientInfo;
import com.neuma573.autoboard.security.service.AuthService;
import com.neuma573.autoboard.security.utils.JwtProvider;
import com.neuma573.autoboard.user.model.dto.GoogleUserResponse;
import com.neuma573.autoboard.user.model.dto.NaverUserResponse;
import com.neuma573.autoboard.user.model.dto.OAuthUserRequest;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.model.enums.AuthenticationProviderType;
import com.neuma573.autoboard.user.service.OAuthService;
import com.neuma573.autoboard.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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

    private final ResponseUtils responseUtils;

    private final JwtProvider jwtProvider;

    private final AuthService authService;

    private final UserService userService;

    @GetMapping("/naver/callback")
    public ResponseEntity<Void> getNaverOAuthCallback(
            @RequestParam(value = "code") String code,
            @RequestParam(value = "state") String state,
            HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest
    )
    {
        NaverUserResponse naverUserResponse = oAuthService.getAuthenticate(code, state);
        return handleLoginAndRedirect(
                naverUserResponse.getId(),
                naverUserResponse.getEmail(),
                state,
                AuthenticationProviderType.NAVER,
                httpServletResponse,
                httpServletRequest
        );
    }

    @GetMapping("/google/callback")
    public ResponseEntity<Void> getGoogleOAuthCallback(
            @RequestParam(value = "code") String code,
            @RequestParam(value = "state", required = false) String state,
            HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest
    )
    {
        GoogleUserResponse googleUserResponse = oAuthService.getAuthenticate(code);
        return handleLoginAndRedirect(
                googleUserResponse.getId(),
                googleUserResponse.getEmail(),
                state,
                AuthenticationProviderType.GOOGLE,
                httpServletResponse,
                httpServletRequest
        );
    }

    @PostMapping(value = "/user")
    public ResponseEntity<Response<UserResponse>> oauthJoin(@Valid @RequestBody OAuthUserRequest oAuthUserRequest) {
        return ResponseEntity.created(URI.create("/main")).body(responseUtils.created(oAuthService.signUp(oAuthUserRequest)));
    }

    private ResponseEntity<Void> handleLoginAndRedirect(
            String providerId,
            String email,
            String redirectUrl,
            AuthenticationProviderType authenticationProviderType,
            HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest
    )
    {
        String joinUuid = oAuthService.handleOAuthLogin(providerId, email, authenticationProviderType);
        if (!joinUuid.isEmpty()) {
            String encodedUuid = URLEncoder.encode(joinUuid, StandardCharsets.UTF_8);
            URI joinUri = URI.create("/api/v1/auth/oauth?code=" + encodedUuid);
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(joinUri).build();
        } else {
            User user = userService.getUserByEmail(email);
            authService.handleLogin(
                    email,
                    ClientInfo.of(httpServletRequest),
                    user
            );
            AccessTokenResponse accessTokenResponse = jwtProvider.createJwt(httpServletResponse, user.getId());
            String finalRedirectUrl = "/oauth-redirect?token=" + accessTokenResponse.getAccessToken() + "&email=" + email;
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                finalRedirectUrl += "&redirect=" + URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8);
            }
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create(finalRedirectUrl)).build();
        }
    }
}

