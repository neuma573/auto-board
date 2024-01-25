package com.neuma573.autoboard.security.utils;

import com.neuma573.autoboard.global.exception.UserNotFoundException;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.security.model.dto.AccessTokenResponse;
import com.neuma573.autoboard.security.model.entity.RefreshToken;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private int refreshTokenExpireTime;

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

    public AccessTokenResponse createJwt(HttpServletResponse httpServletResponse, Long userId) {
        Claims accessTokenClaims = Jwts.claims()
                .subject(String.valueOf(userId))
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
                        (int) (accessTokenExpirationMs / 1000),
                        true,
                        true
                )
        );

        String initialUuid = UUID.randomUUID().toString();

        CookieUtils.addCookie(httpServletResponse,
                CookieUtils.createCookie(
                        "uuid",
                        initialUuid,
                        (int) (refreshTokenExpirationMs / 1000),
                        true,
                        true
                )
        );
        String refreshToken = createRefreshToken(refreshTokenClaims);

        refreshTokenRedisTemplate.opsForValue().set(initialUuid,
                RefreshToken.builder()
                        .uuid(initialUuid)
                        .token(refreshToken)
                        .id(userId)
                        .build(),
                refreshTokenExpireTime,
                TimeUnit.MILLISECONDS
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
            } else if (!"access".equals(claims.get("type"))) {
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

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException | IllegalArgumentException |
                 ExpiredJwtException | UnsupportedJwtException | NoSuchElementException e) {
            return false;
        }

    }

    public boolean validateRefreshToken(String refreshToken, HttpServletResponse response) throws IOException {
        try {

            getClaims(refreshToken);

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

    public Optional<String> parseJwtToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(headerString))
                .filter(StringUtils::hasText)
                .filter(token -> token.startsWith(tokenPrefix))
                .map(token -> token.replace(tokenPrefix, ""))
                .or(() -> CookieUtils.getCookieValue(request, "accessToken"));
    }


    public AccessTokenResponse refreshAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {

        Optional<String> uuid = CookieUtils.getCookieValue(httpServletRequest, "uuid");

        if (uuid.isEmpty()) {
            throw new JwtException("UUID is missing or empty");
        }
        log.info("TOKEN REFRESH ATTEMPT :: UUID :: {}", uuid);
        RefreshToken refreshToken = refreshTokenRedisTemplate.opsForValue().get(uuid.get());
        if (refreshToken == null) {
            throw new JwtException("Refresh Token not found");
        }
        if (!validateRefreshToken(refreshToken.getToken(), httpServletResponse)) {
            throw new JwtException("Invalid refresh token");
        }
        Claims accessTokenClaims = Jwts.claims()
                .subject(String.valueOf(refreshToken.getId()))
                .add("type", "access")
                .build();

        String accessToken = createAccessToken(accessTokenClaims);
        CookieUtils.deleteCookie(httpServletResponse, "accessToken");

        CookieUtils.addCookie(httpServletResponse,
                CookieUtils.createCookie(
                        "accessToken",
                        accessToken,
                        (int) (accessTokenExpirationMs / 1000),
                        true,
                        true
                )
        );
        return AccessTokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    public Long parseUserId(HttpServletRequest httpServletRequest) {
        Optional<Long> userId = parseIdWithValidation(httpServletRequest);
        if (userId.isEmpty()) {
            throw new UserNotFoundException("User ID not found in JWT token");
        }
        return userId.get();
    }

    public Long parseUserIdSafely(HttpServletRequest httpServletRequest) {
        try {
            return parseJwtToken(httpServletRequest)
                    .map(this::getClaims)
                    .map(Claims::getSubject)
                    .map(Long::valueOf)
                    .orElse(-1L);
        } catch (IllegalArgumentException | JwtException e) {
            return -1L;
        }
    }


    public Optional<Long> parseIdWithValidation(HttpServletRequest httpServletRequest) {
        return parseJwtToken(httpServletRequest)
                .map(this::getClaims)
                .map(Claims::getSubject)
                .flatMap(this::parseLongSafely);
    }

    private Optional<Long> parseLongSafely(String subject) {
        try {
            return Optional.of(Long.parseLong(subject));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
