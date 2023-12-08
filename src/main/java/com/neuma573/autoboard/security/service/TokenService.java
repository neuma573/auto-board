package com.neuma573.autoboard.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuma573.autoboard.security.filter.VerifyUserFilter;
import com.neuma573.autoboard.security.model.dto.AuthenticateUser;
import com.neuma573.autoboard.security.model.dto.Jwt;
import com.neuma573.autoboard.security.model.entity.RefreshToken;
import com.neuma573.autoboard.security.repository.RefreshTokenRepository;
import com.neuma573.autoboard.security.utils.JwtProvider;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.model.entity.UserRole;
import com.neuma573.autoboard.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private final ObjectMapper objectMapper;

    public void storeRefreshToken(String loginId, String token) {
        RefreshToken refreshToken = RefreshToken
                .builder()
                .loginId(loginId)
                .token(token)
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public Jwt refreshToken(String refreshToken) throws JsonProcessingException {
        Claims claims = jwtProvider.getClaims(refreshToken);

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new EntityNotFoundException("Token not found"));

        User user = userRepository.findByLoginId(token.getLoginId()).orElseThrow(() -> new EntityNotFoundException("User not found"));

        AuthenticateUser authenticateUser = new AuthenticateUser(user.getLoginId(),
                user.getRoles().stream().map(UserRole::getRole).collect(Collectors.toSet()));
        String authenticateUserJson = objectMapper.writeValueAsString(authenticateUser);
        claims.put(VerifyUserFilter.AUTHENTICATE_USER,authenticateUserJson);
        Jwt jwt = jwtProvider.createJwt(claims);
        token.setToken(jwt.getRefreshToken());
        return jwt;
    }
}