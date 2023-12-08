package com.neuma573.autoboard.user.controller;


import com.neuma573.autoboard.user.model.dto.LoginRequest;
import com.neuma573.autoboard.user.model.dto.LoginResponse;
import com.neuma573.autoboard.user.model.dto.UserRequest;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import com.neuma573.autoboard.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
@RestController
public class UserController {

    private final UserService userService;


    @GetMapping(value = "/test")
    public ResponseEntity<String> test() {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/users")
    public ResponseEntity<UserResponse> join(@Valid @RequestBody UserRequest userRequest) {
        return new ResponseEntity<>(userService.signUp(userRequest), HttpStatus.CREATED);
    }

//    @PostMapping(value = "/login")
//    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
//        return new ResponseEntity<>(userService.login(loginRequest), HttpStatus.OK);
//    }
}
