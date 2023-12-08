package com.neuma573.autoboard.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuma573.autoboard.global.exception.ExceptionCode;
import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.security.model.dto.Jwt;
import com.neuma573.autoboard.user.model.enums.Role;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.Key;
import java.util.Date;

import static com.neuma573.autoboard.global.exception.ExceptionCode.*;


@Slf4j
@Component
public class JwtProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Value("${app.jwt.token-prefix}")
    private String tokenPrefix;

    @Value("${app.jwt.header-string}")
    private String headerString;

    private Key key;

    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.objectMapper = new ObjectMapper();
    }

    public String createToken(Claims claims, Date expireDate) {
        return Jwts.builder()
                .claims(claims)
                .expiration(expireDate)
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Jwt createJwt(Claims claims) {
        String accessToken = createToken(claims, getExpireDateAccessToken());
        String refreshToken = createToken(Jwts.claims().build(), getExpireDateRefreshToken());
        return Jwt.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Date getExpireDateAccessToken() {
        return new Date(System.currentTimeMillis() + accessTokenExpirationMs);
    }

    public Date getExpireDateRefreshToken() {
        long expireTimeMils = 1000L * 60 * 60 * 24 * 60;
        return new Date(System.currentTimeMillis() + refreshTokenExpirationMs);
    }


    public boolean validateToken(String authToken, HttpServletResponse response) throws IOException {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException | IllegalArgumentException e) {
            setResponse(response, INVALID_JWT_TOKEN, e);
        } catch (ExpiredJwtException e) {
            setResponse(response, EXPIRED_ACCESS_TOKEN, e);
        } catch (UnsupportedJwtException e) {
            setResponse(response, UNSUPPORTED_JWT_TOKEN, e);
        }

        return false;
    }

    public String parseJwtToken(HttpServletRequest request) {
        String token = request.getHeader(headerString);
        if (StringUtils.hasText(token) && token.startsWith(tokenPrefix)) {
            token = token.replace(tokenPrefix, "");
        }
        return token;
    }

    private void setResponse(HttpServletResponse response, ExceptionCode exceptionCode, Exception e) throws IOException {
        log.error("error message {}", exceptionCode.getMessage(), e);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Response<Void> fail = Response.error(exceptionCode.getStatus().toString(), exceptionCode.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(fail));
    }

}
