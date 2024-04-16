package com.neuma573.autoboard.security.service;

import com.neuma573.autoboard.global.exception.InvalidLoginException;
import com.neuma573.autoboard.global.exception.NotActivatedUserException;
import com.neuma573.autoboard.global.exception.TooManyLoginAttemptException;
import com.neuma573.autoboard.global.exception.UserBlockedException;
import com.neuma573.autoboard.global.model.enums.Status;
import com.neuma573.autoboard.security.model.dto.ClientInfo;
import com.neuma573.autoboard.security.model.entity.LoginLog;
import com.neuma573.autoboard.security.repository.LoginLogRepository;
import com.neuma573.autoboard.security.utils.PasswordEncoder;
import com.neuma573.autoboard.user.model.dto.LoginRequest;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.repository.UserRepository;
import com.neuma573.autoboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final LoginLogRepository loginLogRepository;

    private final RedisTemplate<String, String> redisTemplate;

    private final UserService userService;

    private final String FAIL_STATE = "FAIL";

    private final String SUCCESS_STATE = "SUCCESS";

    @Transactional
    public User verifyUser(LoginRequest loginRequest, ClientInfo clientInfo) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidLoginException("Invalid email or password"));
        if (user != null && user.getStatus().equals(Status.BANNED.getStatus())) {
            throw new UserBlockedException(loginRequest.getEmail());
        }
        checkLoginAttempts(user);
        try {
            if (isActivatedUser(Objects.requireNonNull(user))) {
                validatePassword(loginRequest.getPassword(), user.getPassword());
                return handleLogin(loginRequest.getEmail(), clientInfo, user);
            } else {
                throw new NotActivatedUserException("not activated");
            }
        } catch (InvalidLoginException ex) {
            handleInvalidLogin(user, loginRequest.getEmail(), clientInfo, ex);
            throw ex;
        } catch (NotActivatedUserException ex) {
            throw ex;
        } catch (Exception ex) {
            recordLoginAttempt(loginRequest.getEmail(), clientInfo, FAIL_STATE, ex);
            throw ex;
        }
    }

    private void checkLoginAttempts(User user) throws TooManyLoginAttemptException {
        if (user != null && user.getFailCount() >= 5) {
            throw new TooManyLoginAttemptException("Excessive login attempts");
        }
    }

    private void validatePassword(String rawPassword, String encodedPassword)  {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new InvalidLoginException("Invalid user or password");
        }
    }

    public void recordLoginAttempt(String email, ClientInfo clientInfo, String result, Exception ex) {

        LoginLog loginLog = LoginLog.builder()
                .email(email)
                .loginTime(LocalDateTime.now())
                .ipAddress(clientInfo.getClientIpAddress())
                .loginResult(result)
                .deviceInfo(clientInfo.getUserAgent())
                .errorMessage(ex != null ? ex.getClass().getSimpleName() + ": " + ex.getMessage() : null)
                .build();
        loginLogRepository.save(loginLog);
    }

    public void handleInvalidLogin(User user, String email, ClientInfo clientInfo, InvalidLoginException ex) {
        recordLoginAttempt(email, clientInfo, FAIL_STATE, ex);
        user.addFailCount();
        userRepository.save(user);
    }

    @Transactional
    public User handleLogin(
            String email,
            ClientInfo clientInfo,
            User user
    ) {
        recordLoginAttempt(email, clientInfo, SUCCESS_STATE, null);
        updateLoginAt(user);
        trackSuccessfulLogin(email);
        return user;
    }

    public void updateLoginAt(User user) {
        user.setLoginAt();
        userRepository.save(user);
    }

    public boolean isActivatedUser(User user) {
        return Objects.equals(user.getStatus(), Status.ACTIVE.getStatus());
    }

    private void trackSuccessfulLogin(String email) {
        String key = "loginSuccessCount:" + email;
        Long count = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofSeconds(1));
        if (count != null && count >= 2) {
            userRepository.findByEmail(email).ifPresent(user -> userService.setBan(user.getId()));
        }
    }

}
