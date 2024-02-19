package com.neuma573.autoboard.security.service;

import com.neuma573.autoboard.global.exception.InvalidLoginException;
import com.neuma573.autoboard.global.exception.NotActivatedUserException;
import com.neuma573.autoboard.global.exception.TooManyLoginAttemptException;
import com.neuma573.autoboard.global.exception.UserBlockedException;
import com.neuma573.autoboard.global.model.enums.Status;
import com.neuma573.autoboard.security.model.dto.AccessTokenResponse;
import com.neuma573.autoboard.security.model.entity.LoginLog;
import com.neuma573.autoboard.security.model.entity.RefreshToken;
import com.neuma573.autoboard.security.repository.LoginLogRepository;
import com.neuma573.autoboard.security.utils.CookieUtils;
import com.neuma573.autoboard.security.utils.JwtProvider;
import com.neuma573.autoboard.security.utils.PasswordEncoder;
import com.neuma573.autoboard.user.model.dto.LoginRequest;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.repository.UserRepository;
import com.neuma573.autoboard.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private final LoginLogRepository loginLogRepository;

    private final RedisTemplate<String, RefreshToken> refreshTokenRedisTemplate;

    private final RedisTemplate<String, String> redisTemplate;

    private final UserService userService;

    private final String FAIL_STATE = "FAIL";

    private final String SUCCESS_STATE = "SUCCESS";

    public AccessTokenResponse verifyUser(LoginRequest loginRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidLoginException("Invalid email or password"));
        if (user != null && user.getStatus().equals(Status.BANNED.getStatus())) {
            throw new UserBlockedException(loginRequest.getEmail());
        }
        checkLoginAttempts(user);
        try {
            if (isActivatedUser(Objects.requireNonNull(user))) {
                validatePassword(loginRequest.getPassword(), user.getPassword());
                recordLoginAttempt(loginRequest, httpServletRequest, SUCCESS_STATE, null);
                updateLoginAt(user);
                trackSuccessfulLogin(loginRequest.getEmail());
                return jwtProvider.createJwt(httpServletResponse, user.getId());
            } else {
                throw new NotActivatedUserException("not activated");
            }
        } catch (InvalidLoginException ex) {
            handleInvalidLogin(user, loginRequest, httpServletRequest, ex);
            throw ex;
        } catch (NotActivatedUserException ex) {
            throw ex;
        } catch (Exception ex) {
            recordLoginAttempt(loginRequest, httpServletRequest, FAIL_STATE, ex);
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

    public void recordLoginAttempt(LoginRequest loginRequest, HttpServletRequest httpServletRequest, String result, Exception ex) {

        LoginLog loginLog = LoginLog.builder()
                .email(loginRequest.getEmail())
                .loginTime(LocalDateTime.now())
                .ipAddress(getClientIpAddress(httpServletRequest))
                .loginResult(result)
                .deviceInfo(httpServletRequest.getHeader("User-Agent"))
                .errorMessage(ex != null ? ex.getClass().getSimpleName() + ": " + ex.getMessage() : null)
                .build();
        loginLogRepository.save(loginLog);
    }

    public String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null) {
            return xForwardedForHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    public void handleInvalidLogin(User user, LoginRequest loginRequest, HttpServletRequest httpServletRequest, InvalidLoginException ex) {
        recordLoginAttempt(loginRequest, httpServletRequest, FAIL_STATE, ex);
        user.addFailCount();
        userRepository.save(user);
    }

    public void updateLoginAt(User user) {
        user.setLoginAt();
        userRepository.save(user);
    }

    public boolean isActivatedUser(User user) {
        return Objects.equals(user.getStatus(), Status.ACTIVE.getStatus());
    }

    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        CookieUtils.getCookieValue(httpServletRequest, "uuid")
                .ifPresent(refreshTokenRedisTemplate::delete);
        CookieUtils.deleteCookie(httpServletResponse, "accessToken");
        CookieUtils.deleteCookie(httpServletResponse, "uuid");
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
