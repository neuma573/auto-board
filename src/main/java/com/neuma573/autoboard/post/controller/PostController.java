package com.neuma573.autoboard.post.controller;

import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.post.model.dto.PostRequest;
import com.neuma573.autoboard.post.model.dto.PostResponse;
import com.neuma573.autoboard.post.service.PostService;
import com.neuma573.autoboard.security.utils.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
@RestController
public class PostController {

    private final JwtProvider jwtProvider;

    private final ResponseUtils responseUtils;

    private final PostService postService;

    @PostMapping("")
    public ResponseEntity<Response<PostResponse>> saveBoard(@Valid @RequestBody PostRequest postRequest, HttpServletRequest httpServletRequest) {
        String email = jwtProvider.parseEmailWithValidation(httpServletRequest);
        return ResponseEntity.created(URI.create("/main")).body(responseUtils.created(postService.savePost(email, postRequest)));
    }
}
