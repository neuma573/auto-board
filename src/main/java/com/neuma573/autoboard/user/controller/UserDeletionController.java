package com.neuma573.autoboard.user.controller;

import com.neuma573.autoboard.security.utils.JwtProvider;
import com.neuma573.autoboard.user.service.UserDeletionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserDeletionController {
    private final JwtProvider jwtProvider;
    private final UserDeletionService userDeletionService;

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(HttpServletRequest request) {
        Long userId = jwtProvider.parseUserId(request);
        return ResponseEntity.status(
                userDeletionService.deleteUser(userId)
        ).build();
    }
}
