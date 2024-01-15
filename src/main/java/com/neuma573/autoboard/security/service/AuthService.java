package com.neuma573.autoboard.security.service;

import com.neuma573.autoboard.global.exception.InvalidLoginException;
import com.neuma573.autoboard.global.exception.NotActivatedUserException;
import com.neuma573.autoboard.global.exception.TokenNotFoundException;
import com.neuma573.autoboard.global.exception.TooManyLoginAttemptException;
import com.neuma573.autoboard.security.model.dto.AccessTokenResponse;
import com.neuma573.autoboard.security.model.entity.LoginLog;
import com.neuma573.autoboard.security.repository.LoginLogRepository;
import com.neuma573.autoboard.security.repository.RefreshTokenRepository;
import com.neuma573.autoboard.security.utils.CookieUtils;
import com.neuma573.autoboard.security.utils.JwtProvider;
import com.neuma573.autoboard.security.utils.PasswordEncoder;
import com.neuma573.autoboard.user.model.dto.LoginRequest;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private final LoginLogRepository loginLogRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final String FAIL_STATE = "FAIL";

    private final String SUCCESS_STATE = "SUCCESS";

    private final String ACTIVATED_STATE = "Active";

    private final String INACTIVATED_STATE = "Inactive";



    public AccessTokenResponse verifyUser(LoginRequest loginRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidLoginException("Invalid email or password"));

        try {
            checkLoginAttempts(user);

            if (isActivatedUser(user)) {
                validatePassword(loginRequest.getPassword(), user.getPassword());
                recordLoginAttempt(loginRequest, httpServletRequest, SUCCESS_STATE, null);
                updateLoginAt(user);

                return jwtProvider.createJwt(loginRequest, httpServletResponse);
            } else {
                throw new NotActivatedUserException("not activatd");
            }
        } catch (InvalidLoginException ex) {
            handleInvalidLogin(user, loginRequest, httpServletRequest, ex);
            throw ex;
        } catch (Exception ex) {
            recordLoginAttempt(loginRequest, httpServletRequest, FAIL_STATE, ex);
            throw ex;
        }
    }

    private void checkLoginAttempts(User user) throws TooManyLoginAttemptException {
        if (user.getFailCount() >= 5) {
            throw new TooManyLoginAttemptException("Excessive login attempts");
        }
    }

    private void validatePassword(String rawPassword, String encodedPassword)  {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new InvalidLoginException("Invalid user or password");
        }
    }

    @Transactional
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

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null) {
            return xForwardedForHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    @Transactional
    public void handleInvalidLogin(User user, LoginRequest loginRequest, HttpServletRequest httpServletRequest, InvalidLoginException ex) {
        recordLoginAttempt(loginRequest, httpServletRequest, FAIL_STATE, ex);
        user.addFailCount();
        userRepository.save(user);
    }

    @Transactional
    public void updateLoginAt(User user) {
        user.setLoginAt();
        userRepository.save(user);
    }

    @Transactional
    public boolean isActivatedUser(User user) {
        return switch (user.getStatus()) {
            case ACTIVATED_STATE -> true;
            case INACTIVATED_STATE -> false;
            default -> false;
        };
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtils.getCookieValue(request, "refreshToken");

        if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenRepository.delete(
                    refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new TokenNotFoundException("No token"))
            );
        }

        CookieUtils.deleteCookie(response, "refreshToken");

    }

}
