package com.neuma573.autoboard.user.service;

import com.neuma573.autoboard.security.model.dto.VerifyResponse;
import com.neuma573.autoboard.security.utils.JwtProvider;
import com.neuma573.autoboard.user.model.dto.LoginRequest;
import com.neuma573.autoboard.user.model.dto.LoginResponse;
import com.neuma573.autoboard.user.model.dto.UserRequest;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.model.entity.UserRole;
import com.neuma573.autoboard.user.model.enums.Role;
import com.neuma573.autoboard.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public UserResponse signUp(UserRequest userRequest) {

        userRequest.setPassword(
                passwordEncoder.encode(
                        userRequest.getPassword()
                )
        );

        User user = userRepository.save(
                userRequest.toEntity()
        );

        UserRole role = UserRole.builder()
                .role(Role.USER)
                .user(user)
                .build();
        user.addRole(role);
        userRoleRepository.save(role);
        return user.toResponse();

    }

    @Transactional
    public LoginResponse signIn(LoginRequest loginRequest) {

        User user = userRepository.findByLoginId(loginRequest.getLoginId()).orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return null;
    }

    @Transactional
    public VerifyResponse verifyUser(LoginRequest loginRequest){
        User user = userRepository.findByLoginId(loginRequest.getLoginId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            user.addFailCount();
            return VerifyResponse
                    .builder()
                    .isValid(false)
                    .build();
            throw new BadCredentialsException("Invalid password");
        }

        return VerifyResponse
                .builder()
                .isValid(true)
                .userRole(user.getRoles().stream().map(UserRole::getRole).collect(Collectors.toSet()))
                .build();
    }

}
