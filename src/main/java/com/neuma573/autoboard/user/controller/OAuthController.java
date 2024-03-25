package com.neuma573.autoboard.user.controller;

import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.user.model.dto.GoogleUserResponse;
import com.neuma573.autoboard.user.model.dto.NaverUserResponse;
import com.neuma573.autoboard.user.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
@Slf4j
@RestController
public class OAuthController {

    private final ResponseUtils responseUtils;

    private final OAuthService oAuthService;

    @GetMapping("/naver/callback")
    public ResponseEntity<Response<NaverUserResponse>> getNaverOAuthCallback(
            @RequestParam(value = "code") String code,
            @RequestParam(value = "state") String state)
    {
        return ResponseEntity.ok().body(responseUtils.created(oAuthService.getAuthenticate(code, state)));
    }

    @GetMapping("/google/callback")
    public ResponseEntity<Response<GoogleUserResponse>> getGoogleOAuthCallback(
            @RequestParam(value = "code") String code,
            @RequestParam(value = "state") String state,
            @RequestParam(value = "scope") String scope)
    {
        return ResponseEntity.ok().body(responseUtils.created(oAuthService.getAuthenticate(code, state, scope)));
    }

}

