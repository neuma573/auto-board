package com.neuma573.autoboard.global.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuma573.autoboard.global.exception.ExceptionCode;
import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.security.model.dto.LoginUser;
import com.neuma573.autoboard.user.model.entity.User;
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
public class JwtUtils {

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

    public String generateJwtToken(String username) {
        return Jwts.builder().subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + accessTokenExpirationMs))
                .signWith(key)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {

        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

    }

    public boolean validateJwtToken(String authToken, HttpServletResponse response) throws IOException {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
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

    public LoginUser verify(String token) {

        Claims claims = Jwts.parser().verifyWith((SecretKey) key).build().parseUnsecuredClaims(token).getPayload();

        Long id = claims.get("id", Long.class);
        String email = claims.get("email", String.class);
        String loginId = claims.get("loginId", String.class);
        String name = claims.get("name", String.class);
        String role = claims.get("role", String.class);

        User user = User.builder()
                .id(id)
                .loginId(loginId)
                .name(name)
                .role(Role.valueOf(role))
                .build();

        return new LoginUser(user);
    }
}
