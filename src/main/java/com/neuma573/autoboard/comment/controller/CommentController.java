package com.neuma573.autoboard.comment.controller;

import com.neuma573.autoboard.comment.model.dto.CommentRequest;
import com.neuma573.autoboard.comment.model.dto.CommentResponse;
import com.neuma573.autoboard.comment.service.CommentService;
import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
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

@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    private final JwtProvider jwtProvider;

    private final ResponseUtils responseUtils;

    @PostMapping("")
    public ResponseEntity<Response<CommentResponse>> savePost(
            @RequestBody @Valid CommentRequest commentRequest,
            HttpServletRequest httpServletRequest
            ) {
        Long userId = jwtProvider.parseUserId(httpServletRequest);

        return ResponseEntity.created(URI.create("/main")).body(responseUtils.success(commentService.savePost(commentRequest, userId)));
    }

}
