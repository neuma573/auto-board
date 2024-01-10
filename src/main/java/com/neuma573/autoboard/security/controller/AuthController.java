package com.neuma573.autoboard.security.controller;

import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.security.model.dto.AccessTokenResponse;
import com.neuma573.autoboard.security.service.AuthService;
import com.neuma573.autoboard.security.service.TokenService;
import com.neuma573.autoboard.user.model.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@RestController
public class AuthController {

    private final TokenService tokenService;

    private final AuthService authService;

    private final ResponseUtils responseUtils;

    @PostMapping("/refresh/token")
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
}
