package com.neuma573.autoboard.security.service;

import com.neuma573.autoboard.global.exception.InvalidLoginException;
import com.neuma573.autoboard.global.exception.TooManyLoginAttemptException;
import com.neuma573.autoboard.security.model.dto.Jwt;
import com.neuma573.autoboard.security.model.entity.LoginLog;
import com.neuma573.autoboard.security.repository.LoginLogRepository;
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

    private final String FAIL = "FAIL";

    private final String SUCCESS = "SUCCESS";



    public Jwt verifyUser(LoginRequest loginRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        User user = userRepository.findByLoginId(loginRequest.getLoginId())
                .orElseThrow(() -> new InvalidLoginException("Invalid user or password"));

        try {
            checkLoginAttempts(user);
            validatePassword(loginRequest.getPassword(), user.getPassword());
            recordLoginAttempt(loginRequest, httpServletRequest, SUCCESS, null);
            updateLoginAt(user);

            return jwtProvider.createJwt(loginRequest, httpServletResponse);

        } catch (InvalidLoginException ex) {
            handleInvalidLogin(user, loginRequest, httpServletRequest, ex);
            throw ex;
        } catch (Exception ex) {
            recordLoginAttempt(loginRequest, httpServletRequest, FAIL, ex);
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
                .loginId(loginRequest.getLoginId())
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
        recordLoginAttempt(loginRequest, httpServletRequest, FAIL, ex);
        user.addFailCount();
        userRepository.save(user);
    }

    @Transactional
    public void updateLoginAt(User user) {
        user.setLoginAt();
        userRepository.save(user);
    }

}
