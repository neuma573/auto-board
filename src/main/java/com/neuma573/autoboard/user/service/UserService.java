package com.neuma573.autoboard.user.service;

import com.neuma573.autoboard.user.model.dto.UserRequest;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import com.neuma573.autoboard.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse join(UserRequest userRequest) {

        userRequest.setPassword(
                passwordEncoder.encode(
                        userRequest.getPassword()
                )
        );

        userRepository.save(
                userRequest.toEntity()
        );
        return userRepository.findByLoginId(userRequest.getLoginId()).get();
    }
}
