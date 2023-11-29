package com.neuma573.autoboard.security.service;

import com.neuma573.autoboard.security.model.dto.LoginUser;
import com.neuma573.autoboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        return LoginUser.builder()
                .user(
                        userRepository.findByLoginId(loginId)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found with loginId: " + loginId))
                )
                .build();
    }
}
