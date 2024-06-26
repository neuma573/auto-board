package com.neuma573.autoboard.like.controller;

import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.like.model.dto.LikeResponse;
import com.neuma573.autoboard.like.service.LikeService;
import com.neuma573.autoboard.post.model.annotation.CheckPostAccess;
import com.neuma573.autoboard.post.model.enums.PostAction;
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

    @CheckPostAccess(action = PostAction.READ)
    @GetMapping("/{postId}")
    public ResponseEntity<Response<Long>> countLikes(@PathVariable Long postId) {
        return ResponseEntity.ok().body(responseUtils.success(likeService.getLikeCount(postId)));
    }

    @CheckPostAccess(action = PostAction.READ)
    @PostMapping("/{postId}")
    public ResponseEntity<Response<LikeResponse>> toggleLike(@PathVariable Long postId, HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.parseUserId(httpServletRequest);
        return ResponseEntity.ok().body(responseUtils.success(likeService.toggleLike(postId, userId)));
    }

}
