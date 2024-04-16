package com.neuma573.autoboard.security.controller;

import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.security.model.dto.AccessTokenRequest;
import com.neuma573.autoboard.security.model.dto.AccessTokenResponse;
import com.neuma573.autoboard.security.model.entity.RefreshToken;
import com.neuma573.autoboard.security.service.AuthService;
import com.neuma573.autoboard.security.service.TokenService;
import com.neuma573.autoboard.security.utils.CookieUtils;
import com.neuma573.autoboard.user.model.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@RestController
public class AuthController {

    private final TokenService tokenService;

    private final AuthService authService;

    private final ResponseUtils responseUtils;

    private final RedisTemplate<String, RefreshToken> refreshTokenRedisTemplate;

    @PutMapping("/refresh/token")
    public ResponseEntity<Response<AccessTokenResponse>> tokenRefresh(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        return ResponseEntity.ok(responseUtils.success(
                tokenService.refreshAccessToken(httpServletRequest, httpServletResponse)
        ));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Response<?>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(responseUtils.success(
                authService.verifyUser(loginRequest, httpServletRequest, httpServletResponse)
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<String>> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        CookieUtils.getCookieValue(httpServletRequest, "uuid")
                .ifPresent(uuid -> {
                    refreshTokenRedisTemplate.delete(uuid);
                    CookieUtils.deleteCookie(httpServletResponse, "uuid");
                });
        CookieUtils.getCookieValue(httpServletRequest, "accessToken")
                .ifPresent(accessToken -> CookieUtils.deleteCookie(httpServletResponse, "accessToken"));
        return ResponseEntity.ok(responseUtils.success("로그아웃 성공"));
    }

    @PostMapping("/verify-token")
    public ResponseEntity<Response<Boolean>> verify(@RequestBody AccessTokenRequest accessTokenRequest) {
        return ResponseEntity.ok(responseUtils.success(tokenService.validateToken(accessTokenRequest)));
    }
}
