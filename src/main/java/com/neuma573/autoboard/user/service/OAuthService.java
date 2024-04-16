package com.neuma573.autoboard.user.service;

import com.neuma573.autoboard.email.service.MailService;
import com.neuma573.autoboard.global.client.GoogleAuthClient;
import com.neuma573.autoboard.global.client.GoogleUserClient;
import com.neuma573.autoboard.global.client.NaverAuthClient;
import com.neuma573.autoboard.global.client.NaverUserClient;
import com.neuma573.autoboard.user.model.dto.*;
import com.neuma573.autoboard.user.model.entity.AuthenticationProvider;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.model.entity.UserRole;
import com.neuma573.autoboard.user.model.enums.AuthenticationProviderType;
import com.neuma573.autoboard.user.model.enums.Role;
import com.neuma573.autoboard.user.repository.AuthenticationProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuthService {

    private final NaverAuthClient naverAuthClient;

    private final NaverUserClient naverUserClient;

    private final GoogleAuthClient googleAuthClient;

    private final GoogleUserClient googleUserClient;

    private final AuthenticationProviderRepository authenticationProviderRepository;

    private final UserService userService;

    private final RedisTemplate<String, ProviderUserResponse> providerUserResponseRedisTemplate;

    @Value("${app.oauth2.naver.client-id}")
    private String naverClientId;

    @Value("${app.oauth2.naver.client-secret}")
    private String naverClientSecret;

    @Value("${app.oauth2.google.client-id}")
    private String googleClientId;

    @Value("${app.oauth2.google.client-secret}")
    private String googleClientSecret;

    @Value("${app.domain}")
    private String domain;

    public NaverUserResponse getAuthenticate(String code, String state) {
        NaverTokenResponse tokenResponse = getToken(code, state);
        return getNaverUserResponse(tokenResponse);
    }

    public GoogleUserResponse getAuthenticate(String code) {
        GoogleTokenResponse tokenResponse = getToken(code);
        return getGoogleUserResponse(tokenResponse);
    }

    private NaverTokenResponse getToken(String code, String state) {
        return naverAuthClient.getToken(
                "authorization_code",
                naverClientId,
                naverClientSecret,
                code,
                state
        );
    }

    private GoogleTokenResponse getToken(String code) {
        return googleAuthClient.getToken(
                code,
                googleClientId,
                googleClientSecret,
                domain + "/api/v1/oauth2/google/callback",
                "authorization_code"
        );
    }

    private NaverUserResponse getNaverUserResponse(NaverTokenResponse tokenResponse) {
        return naverUserClient.getNaverUserResponse(getAuthorizationHeader(tokenResponse));
    }

    private GoogleUserResponse getGoogleUserResponse(GoogleTokenResponse tokenResponse) {
        return googleUserClient.getGoogleUserResponse(getAuthorizationHeader(tokenResponse));
    }

    private String getAuthorizationHeader(TokenResponse tokenResponse) {
        return tokenResponse.getTokenType() + " " + tokenResponse.getAccessToken();
    }

    public String handleOAuthLogin(String providerId, String email, AuthenticationProviderType authenticationProviderType) {
        Optional<AuthenticationProvider> authenticationProvider = getAuthenticationProviderById(providerId);

        if (authenticationProvider.isPresent()) {
            return "";
            // 로그인 처리
        } else {
            boolean isEmailAvailable = userService.isEmailAvailable(email);
            if (isEmailAvailable) {
                return saveUser(providerId, email, authenticationProviderType);
            }

            throw new RuntimeException("이메일이 중복입니다.");
        }
    }

    private Optional<AuthenticationProvider> getAuthenticationProviderById(String providerId) {
        return authenticationProviderRepository.findByProviderId(providerId);
    }

    private String saveUser(String providerId, String email, AuthenticationProviderType authenticationProvider) {
        String uuid = UUID.randomUUID().toString();
        ProviderUserResponse userResponse = ProviderUserResponse
                .builder()
                .providerId(providerId)
                .email(email)
                .authenticationProviderType(authenticationProvider)
                .build();
        providerUserResponseRedisTemplate.opsForValue().set(uuid, userResponse, 1, TimeUnit.HOURS);

        return uuid;
    }

    public ProviderUserResponse getUserByUuid(String uuid) {
        return providerUserResponseRedisTemplate.opsForValue().get(uuid);
    }

    @Transactional
    public UserResponse signUp(OAuthUserRequest oAuthUserRequest) {
        ProviderUserResponse providerUserResponse = getUserByUuid(oAuthUserRequest.getUuid());
        AuthenticationProvider authenticationProvider = AuthenticationProvider.builder()
                .providerId(providerUserResponse.getProviderId())
                .provider(providerUserResponse.getAuthenticationProviderType())
                .build();

        User user = oAuthUserRequest.toEntity(providerUserResponse);
        UserRole role = UserRole.builder()
                .role(Role.USER)
                .user(user)
                .build();
        user.addRole(role);
        user = userService.saveUser(user);
        userService.saveUserRole(role);
        authenticationProvider.setUser(user);
        authenticationProviderRepository.save(authenticationProvider);
        return UserResponse.of(user);
    }
}
