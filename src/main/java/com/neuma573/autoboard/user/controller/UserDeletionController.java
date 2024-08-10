package com.neuma573.autoboard.user.controller;

import com.neuma573.autoboard.user.service.UserDeletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserDeletionController {
    private final UserDeletionService userDeletionService;

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(final Long id) {
        userDeletionService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
