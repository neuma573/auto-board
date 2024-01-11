package com.neuma573.autoboard.user.service;

import com.neuma573.autoboard.security.utils.PasswordEncoder;
import com.neuma573.autoboard.user.model.dto.UserRequest;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.model.entity.UserRole;
import com.neuma573.autoboard.user.model.enums.Role;
import com.neuma573.autoboard.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signUp(UserRequest userRequest) {

        userRequest.setPassword(
                passwordEncoder.encode(
                        userRequest.getPassword()
                )
        );

        User user = userRequest.toEntity();
        UserRole role = UserRole.builder()
                .role(Role.USER)
                .user(user)
                .build();
        user.addRole(role);
        userRepository.save(user);
        userRoleRepository.save(role);
        return user.toResponse();

    }

    @Transactional
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

}
