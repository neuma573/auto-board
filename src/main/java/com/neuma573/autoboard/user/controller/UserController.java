package com.neuma573.autoboard.user.controller;


import com.neuma573.autoboard.user.model.dto.UserRequest;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/v1")
@RestController
public class UserController {

    @PostMapping(value = "/users")
    public ResponseEntity<UserResponse> register() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
