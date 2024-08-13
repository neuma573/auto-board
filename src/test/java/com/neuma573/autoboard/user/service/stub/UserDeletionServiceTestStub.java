package com.neuma573.autoboard.user.service.stub;

import com.neuma573.autoboard.user.model.entity.AuthenticationProvider;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.model.enums.AuthenticationProviderType;
import feign.Request;
import feign.Response;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class UserDeletionServiceTestStub {
    public static final User GOOGLE_USER = User.builder()
            .name("googleTester")
            .email("test@gmail.com")
            .build();
    public static final User NAVER_USER = User.builder()
            .name("naverTester")
            .email("test@naver.com")
            .build();
    public static final AuthenticationProvider GOOGLE_AUTHENTICATION_PROVIDER = AuthenticationProvider.builder()
            .id(1L)
            .provider(AuthenticationProviderType.GOOGLE)
            .providerId(UUID.randomUUID().toString())
            .user(GOOGLE_USER)
            .build();
    public static final AuthenticationProvider NAVER_AUTHENTICATION_PROVIDER = AuthenticationProvider.builder()
            .id(2L)
            .provider(AuthenticationProviderType.GOOGLE)
            .providerId(UUID.randomUUID().toString())
            .user(NAVER_USER)
            .build();
    public static final Request REQUEST = Request.create(
            Request.HttpMethod.GET,
            "",
            Map.of("", List.of("")),
            new byte[]{},
            null,
            null
    );
    public static final Response GOOGLE_RESPONSE = Response.builder()
            .status(HttpStatus.OK.value())
            .headers(Collections.emptyMap())
            .request(REQUEST)
            .build();
    public static final Response NAVER_RESPONSE = Response.builder()
            .status(HttpStatus.NO_CONTENT.value())
            .headers(Collections.emptyMap())
            .request(REQUEST)
            .build();
}
