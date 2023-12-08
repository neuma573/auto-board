package com.neuma573.autoboard.security.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.neuma573.autoboard.security.model.dto.Jwt;
import com.neuma573.autoboard.security.model.dto.TokenRequest;
import com.neuma573.autoboard.security.service.TokenService;
import com.neuma573.autoboard.user.model.dto.LoginRequest;
import com.neuma573.autoboard.user.model.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@RestController
public class AuthController {

    private final TokenService tokenService;

    @PostMapping("/refresh/token")
    public ResponseEntity<Jwt> tokenRefresh(@RequestBody TokenRequest tokenRequest) throws JsonProcessingException {
        Jwt jwt = tokenService.refreshToken(tokenRequest.getRefreshToken());
        if (jwt == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest) {

    }

}
