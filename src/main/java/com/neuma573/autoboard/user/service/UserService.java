package com.neuma573.autoboard.user.service;

import com.neuma573.autoboard.email.model.dto.MailRequest;
import com.neuma573.autoboard.email.service.MailService;
import com.neuma573.autoboard.global.exception.UserNotFoundException;
import com.neuma573.autoboard.global.model.enums.Status;
import com.neuma573.autoboard.security.model.entity.VerificationToken;
import com.neuma573.autoboard.security.utils.PasswordEncoder;
import com.neuma573.autoboard.user.model.dto.UserRequest;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.model.entity.UserRole;
import com.neuma573.autoboard.user.model.enums.Role;
import com.neuma573.autoboard.user.repository.UserRepository;
import com.neuma573.autoboard.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;

    private final RedisTemplate<String, VerificationToken> verificationTokenRedisTemplate;

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
        mailService.sendVerifyEmail(
                MailRequest.builder()
                        .to(user.getEmail())
                        .name(user.getName())
                        .verificationToken(generateVerificationToken(user))
                .build()
        );
        return UserResponse.of(user);
    }

    @Transactional
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    
    public VerificationToken generateVerificationToken(User user) {

        VerificationToken verificationToken = VerificationToken.generateVerificationToken(user);
        verificationTokenRedisTemplate.opsForValue().set(verificationToken.getToken(), verificationToken);
        return verificationToken;

    }

    public boolean activateUserAccount(String token) {
        VerificationToken verificationToken = findByToken(token);
        if (verificationToken == null || !isValidVerificationToken(verificationToken)) {
            return false;
        }

        return activateUser(verificationToken);
    }

    private boolean activateUser(VerificationToken verificationToken) {
        User user = userRepository.findByEmail(verificationToken.getEmail()).orElse(null);
        if (user == null) {
            return false;
        }
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
        verificationTokenRedisTemplate.delete(verificationToken.getToken());
        return true;
    }


    public VerificationToken findByToken(String token) {
        return verificationTokenRedisTemplate.opsForValue().get(token);
    }

    public boolean isValidVerificationToken(VerificationToken verificationToken) {
        return verificationToken.getExpiryDate().isAfter(LocalDateTime.now());
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));
    }

    public User getUserByIdSafely(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }


    @Transactional(readOnly = true)
    public boolean isAdmin(User user) {
        return getRolesById(user.getId()).stream()
                .anyMatch(userRole -> userRole.getRole() == Role.ADMIN);
    }

    public Set<UserRole> getRolesById(Long userId) {
        return userRoleRepository.findByUserId(userId);
    }

    @Transactional
    public void initializeFailCount() {
        userRepository.resetAllUserFailCounts();
    }

    @Transactional
    public void setBan(Long userId) {
        User user = getUserById(userId);
        user.setStatus(Status.BANNED);
    }

}
