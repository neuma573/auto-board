package com.neuma573.autoboard.user.service;

import com.neuma573.autoboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDeletionService {
    private final UserRepository userRepository;

    public void deleteUser(final Long id) {
        userRepository.deleteById(id);
    }
}
