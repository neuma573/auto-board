package com.neuma573.autoboard.like.controller;

import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {

    private final LikeService likeService;

    private final ResponseUtils responseUtils;

    @GetMapping("/{postId}")
    public ResponseEntity<Response<Long>> countLikes(@PathVariable Long postId) {
//        int likeCount = likeService.countLikes(postId);
        return ResponseEntity.ok().body(responseUtils.success(0L));
    }

}
