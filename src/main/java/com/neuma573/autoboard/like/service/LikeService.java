package com.neuma573.autoboard.like.service;

import com.neuma573.autoboard.like.model.dto.LikeRequest;
import com.neuma573.autoboard.like.model.dto.LikeResponse;
import com.neuma573.autoboard.like.model.entity.Like;
import com.neuma573.autoboard.like.repository.LikeRepository;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.post.service.PostService;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;

    private final UserService userService;

    private final PostService postService;

    public LikeResponse createLike(LikeRequest likeRequest, Long userId) {
        User user = userService.getUserById(userId);
        Post post = postService.getPostById(likeRequest.getPostId());
        Like like = likeRepository.save(
                LikeRequest.toEntity(user, post)
        );
        return LikeResponse.of(like);
    }

    public void deleteLike(LikeRequest likeRequest) {

    }

    public Long getLikeCount(LikeRequest likeRequest) {
        return 0L;
    }

}
