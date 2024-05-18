package com.neuma573.autoboard.like.controller;

import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.like.model.dto.LikeResponse;
import com.neuma573.autoboard.like.service.LikeService;
import com.neuma573.autoboard.security.utils.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {

    private final LikeService likeService;

    private final ResponseUtils responseUtils;

    private final JwtProvider jwtProvider;

    @GetMapping("/{postId}")
    public ResponseEntity<Response<Long>> countLikes(@PathVariable Long postId) {
//        int likeCount = likeService.countLikes(postId);
        return ResponseEntity.ok().body(responseUtils.success(0L));
    }

    @PostMapping("/{postId}")
    public ResponseEntity<Response<LikeResponse>> toogleLike(@PathVariable Long postId, HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.parseUserId(httpServletRequest);
        return ResponseEntity.ok().body(responseUtils.success(likeService.toggleLike(postId, userId)));
    }

}
