package com.neuma573.autoboard.user.service;

import com.neuma573.autoboard.global.client.GoogleAuthClient;
import com.neuma573.autoboard.global.client.NaverAuthClient;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.repository.AuthenticationProviderRepository;
import com.neuma573.autoboard.user.repository.UserRepository;
import com.neuma573.autoboard.user.service.stub.UserDeletionServiceTestStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class UserDeletionServiceTest extends UserDeletionServiceTestStub {
    @MockBean
    private GoogleAuthClient googleAuthClient;
    @MockBean
    private NaverAuthClient naverAuthClient;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationProviderRepository authenticationProviderRepository;
    @Autowired
    private UserDeletionService userDeletionService;

    @BeforeEach
    void setUp() {
        userRepository.save(GOOGLE_USER);
        authenticationProviderRepository.save(GOOGLE_AUTHENTICATION_PROVIDER);
        userRepository.save(NAVER_USER);
        authenticationProviderRepository.save(NAVER_AUTHENTICATION_PROVIDER);
    }

    @ParameterizedTest
    @MethodSource(value = "userProvider")
    @DisplayName("회원 탈퇴를 요청하면 성공 상태코드를 반환한다.")
    void deleteUser(User user) {
        //given
        when(googleAuthClient.revoke(anyString()))
                .thenReturn(GOOGLE_RESPONSE);
        when(naverAuthClient.deleteToken(anyString(), anyString(), anyString()))
                .thenReturn(NAVER_RESPONSE);

        //when
        HttpStatus httpStatus = userDeletionService.deleteUser(user.getId());

        //then
        assertThat(httpStatus)
                .isIn(
                        HttpStatus.OK,
                        HttpStatus.NO_CONTENT
                );
    }

    private static User[] userProvider() {
        return new User[]{GOOGLE_USER, NAVER_USER};
    }
}