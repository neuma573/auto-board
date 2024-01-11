package com.neuma573.autoboard.user.controller;


import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.user.model.dto.EmailRequest;
import com.neuma573.autoboard.user.model.dto.UserRequest;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import com.neuma573.autoboard.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@RestController
public class UserController {

    private final UserService userService;

    private final ResponseUtils responseUtils;


    @GetMapping(value = "/test")
    public ResponseEntity<String> test() {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/users")
    public ResponseEntity<Response<UserResponse>> join(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.created(URI.create("/main")).body(responseUtils.created(userService.signUp(userRequest)));
    }

    @GetMapping(value = "/users/email-check")
    public ResponseEntity<Response<?>> checkEmailAvailability(@Valid @ModelAttribute EmailRequest emailRequest) {
        return ResponseEntity.ok().body(responseUtils.success(userService.isEmailAvailable(emailRequest.getEmail())));
    }



}
