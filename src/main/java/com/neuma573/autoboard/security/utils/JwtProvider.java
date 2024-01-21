package com.neuma573.autoboard.security.utils;

import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.security.model.dto.AccessTokenResponse;
import com.neuma573.autoboard.security.model.entity.RefreshToken;
import com.neuma573.autoboard.user.model.dto.LoginRequest;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.Key;
import java.util.Date;

import static com.neuma573.autoboard.global.exception.ExceptionCode.*;


@Slf4j
@RequiredArgsConstructor
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

    @Value("${app.jwt.issuer}")
    private String iss;

    private Key key;

    private final ResponseUtils responseUtils;

    private final RedisTemplate<String, RefreshToken> refreshTokenRedisTemplate;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String createRefreshToken(Claims claims) {
        return Jwts.builder()
                .claims(claims)
                .expiration(getExpireDate(refreshTokenExpirationMs))
                .signWith(key)
                .issuer(iss)
                .issuedAt(new Date())
                .compact();
    }

    public String createAccessToken(Claims claims) {
        return Jwts.builder()
                .claims(claims)
                .expiration(getExpireDate(accessTokenExpirationMs))
                .signWith(key)
                .issuer(iss)
                .issuedAt(new Date())
                .compact();
    }


    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public AccessTokenResponse createJwt(LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        Claims accessTokenClaims = Jwts.claims()
                .subject(loginRequest.getEmail())
                .add("type", "access")
                .build();
        Claims refreshTokenClaims = Jwts.claims()
                .add("type", "refresh")
                .build();
        String accessToken = createAccessToken(accessTokenClaims);
        CookieUtils.addCookie(httpServletResponse,
                CookieUtils.createCookie(
                        "accessToken",
                        accessToken,
                        10 * 60,
                        true,
                        true
                )
        );
        String refreshToken = createRefreshToken(refreshTokenClaims);
        refreshTokenRedisTemplate.delete(loginRequest.getEmail());
        refreshTokenRedisTemplate.opsForValue().set(refreshToken,
                RefreshToken.builder()
                        .email(loginRequest.getEmail())
                        .token(refreshToken)
                        .build()
        );
        return AccessTokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    public Date getExpireDate(Long expirationMs) {
        return new Date(System.currentTimeMillis() + expirationMs);
    }

    public boolean validateAccessToken(String accessToken, HttpServletResponse httpServletResponse) throws IOException {
        try {

            Claims claims = getClaims(accessToken);

            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(accessToken);

            if (!iss.equals(claims.getIssuer())) {
                responseUtils.setResponse(httpServletResponse, INVALID_JWT_ISSUER, null);
                return false;
            } else if(!"access".equals(claims.get("type"))) {
                throw new io.jsonwebtoken.security.SecurityException("Access Token is not valid");
            }
            return true;

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException | IllegalArgumentException e) {
            responseUtils.setResponse(httpServletResponse, INVALID_JWT_TOKEN, e);
        } catch (ExpiredJwtException e) {
            responseUtils.setResponse(httpServletResponse, EXPIRED_ACCESS_TOKEN, e);
        } catch (UnsupportedJwtException e) {
            responseUtils.setResponse(httpServletResponse, UNSUPPORTED_JWT_TOKEN, e);
        }

        return false;
    }

    public boolean validateAccessTokenWithoutResponse(String accessToken) {
        try {
            Claims claims = getClaims(accessToken);

            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(accessToken);

            return iss.equals(claims.getIssuer()) && "access".equals(claims.get("type"));

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException | IllegalArgumentException | ExpiredJwtException | UnsupportedJwtException e) {
            return false;
        }

    }

    public boolean validateRefreshToken(String refreshToken, HttpServletResponse response) throws IOException {
        try {

            getClaims(refreshToken);
            RefreshToken token = refreshTokenRedisTemplate.opsForValue().get(refreshToken);
            if (token == null) {
                throw new JwtException("Token not found");
            }

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException | IllegalArgumentException e) {
            responseUtils.setResponse(response, INVALID_JWT_TOKEN, e);
        } catch (ExpiredJwtException e) {
            responseUtils.setResponse(response, EXPIRED_ACCESS_TOKEN, e);
        } catch (UnsupportedJwtException e) {
            responseUtils.setResponse(response, UNSUPPORTED_JWT_TOKEN, e);
        }

        return false;
    }

    public String parseJwtToken(HttpServletRequest request) {
        String token = request.getHeader(headerString);
        if (StringUtils.hasText(token) && token.startsWith(tokenPrefix)) {
            token = token.replace(tokenPrefix, "");
        } else {
            token = CookieUtils.getCookieValue(request, "accessToken");
        }
        return token;
    }

    public AccessTokenResponse refreshAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        log.info(parseJwtToken(httpServletRequest));
        String email = parseEmailFrom(httpServletRequest);
        log.info(email);
        RefreshToken refreshToken = refreshTokenRedisTemplate.opsForValue().get(email);
        if (refreshToken == null) {
            CookieUtils.deleteCookie(httpServletResponse, "accessToken");
            throw new JwtException("Refresh Token not found");
        }
        if (validateRefreshToken(refreshToken.getToken(), httpServletResponse)) {
            Claims accessTokenClaims = Jwts.claims()
                    .subject(refreshToken.getEmail())
                    .add("type", "access")
                    .build();

            String accessToken = createAccessToken(accessTokenClaims);
            CookieUtils.deleteCookie(httpServletResponse, "accessToken");

            CookieUtils.addCookie(httpServletResponse,
                    CookieUtils.createCookie(
                            "accessToken",
                            accessToken,
                            10 * 60,
                            true,
                            true
                    )
            );

            return AccessTokenResponse.builder()
                    .accessToken(accessToken)
                    .build();
        }
        else {
            throw new JwtException("invalid refresh token");
        }
    }

    public String parseEmailFrom(HttpServletRequest httpServletRequest) {
        try {
            return getClaims(
                    parseJwtToken(httpServletRequest)
            ).getSubject();
        } catch (IllegalArgumentException | JwtException ignore) {
            return "";
        }
    }

    public String parseEmailWithValidation(HttpServletRequest httpServletRequest) {
        return getClaims(
                parseJwtToken(httpServletRequest)
        ).getSubject();
    }
}
