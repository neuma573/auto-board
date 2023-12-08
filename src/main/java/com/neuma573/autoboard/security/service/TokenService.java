package com.neuma573.autoboard.security.service;

import com.neuma573.autoboard.security.model.entity.RefreshToken;
import com.neuma573.autoboard.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void storeRefreshToken(String userId, String token) {
        RefreshToken refreshToken = RefreshToken
                .builder()
                .userId(userId)
                .token(token)
                .build();
        refreshTokenRepository.save(refreshToken);
    }
}