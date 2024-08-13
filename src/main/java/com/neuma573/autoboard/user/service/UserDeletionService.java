package com.neuma573.autoboard.user.service;

import com.neuma573.autoboard.global.client.GoogleAuthClient;
import com.neuma573.autoboard.global.client.NaverAuthClient;
import com.neuma573.autoboard.global.exception.UserNotFoundException;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.repository.UserRepository;
import feign.Response;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDeletionService {
    private final GoogleAuthClient googleAuthClient;
    private final NaverAuthClient naverAuthClient;
    private final UserRepository userRepository;

    @Value("${app.oauth2.naver.client-id}")
    private String naverClientId;

    @Value("${app.oauth2.naver.client-secret}")
    private String naverClientSecret;

    @Transactional
    public HttpStatus deleteUser(final Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));

        List<HttpStatus> httpStatuses = user.getAuthenticationProviders().stream()
                .map(provider -> switch (provider.getProvider()) {
                    case GOOGLE -> deleteGoogleUser(provider.getProviderId());
                    case NAVER -> deleteNaverUser(provider.getProviderId());
                    default -> HttpStatus.NOT_IMPLEMENTED;
                })
                .toList();

        userRepository.deleteById(id);
        return httpStatuses.get(0);
    }

    @Transactional
    public HttpStatus deleteGoogleUser(final String token) {
        try {
            HttpStatus revokeResult = revoke(token);
            if (revokeResult != HttpStatus.OK) {
                return revokeResult;
            }
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.NO_CONTENT;
    }

    private HttpStatus revoke(final String token) {
        try (Response response = googleAuthClient.revoke(token)) {
            return HttpStatus.valueOf(response.status());
        }
    }

    @Transactional
    public HttpStatus deleteNaverUser(final String token) {
        try {
            HttpStatus deleteResult = delete(token);
            if (deleteResult != HttpStatus.NO_CONTENT) {
                return deleteResult;
            }
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.NO_CONTENT;
    }

    private HttpStatus delete(final String token) {
        try (Response response = naverAuthClient.deleteToken(
                naverClientId,
                naverClientSecret,
                token
        )) {
            return HttpStatus.valueOf(response.status());
        }
    }
}
