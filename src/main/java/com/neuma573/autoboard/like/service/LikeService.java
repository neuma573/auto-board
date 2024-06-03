package com.neuma573.autoboard.like.service;

import com.neuma573.autoboard.like.model.dto.LikeResponse;
import com.neuma573.autoboard.like.model.entity.Like;
import com.neuma573.autoboard.like.repository.LikeRepository;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.post.service.PostService;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;

    private final UserService userService;

    private final PostService postService;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public LikeResponse toggleLike(Long postId, Long userId) {
        User user = userService.getUserById(userId);
        Post post = postService.getPostById(postId);

        Optional<Like> existingLike = likeRepository.findByCreatedByAndPost(user, post);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.decreaseLikes();
        } else {
            likeRepository.save(
                    Like.of(post, user)
            );
            post.increaseLikes();
        }
        return LikeResponse.of(postId, getLikeCount(postId));
    }

    public Long getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

}
