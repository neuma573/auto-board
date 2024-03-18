package com.neuma573.autoboard.user.controller;


import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.security.utils.JwtProvider;
import com.neuma573.autoboard.user.model.dto.EmailRequest;
import com.neuma573.autoboard.user.model.dto.UserRequest;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import com.neuma573.autoboard.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@RestController
public class UserController {

    private final UserService userService;

    private final ResponseUtils responseUtils;

    private final JwtProvider jwtProvider;

    @GetMapping(value = "")
    public ResponseEntity<Response<UserResponse>> getUserInfo(HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.parseUserId(httpServletRequest);
        return ResponseEntity.ok().body(responseUtils.success(userService.getUser(userId)));
    }

    @PostMapping(value = "")
    public ResponseEntity<Response<UserResponse>> join(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.created(URI.create("/main")).body(responseUtils.created(userService.signUp(userRequest)));
    }

    @GetMapping(value = "/email-check")
    public ResponseEntity<Response<?>> checkEmailAvailability(@Valid @ModelAttribute EmailRequest emailRequest) {
        return ResponseEntity.ok().body(responseUtils.success(userService.isEmailAvailable(emailRequest.getEmail())));
    }

}
