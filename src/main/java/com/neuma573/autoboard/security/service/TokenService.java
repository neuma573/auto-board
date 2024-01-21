package com.neuma573.autoboard.security.service;

import com.neuma573.autoboard.security.model.dto.AccessTokenResponse;
import com.neuma573.autoboard.security.utils.JwtProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtProvider jwtProvider;

    @Transactional
    public AccessTokenResponse refreshAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        return jwtProvider.refreshAccessToken(httpServletRequest, httpServletResponse);
    }
}