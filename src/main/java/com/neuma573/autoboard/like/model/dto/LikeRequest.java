package com.neuma573.autoboard.like.model.dto;

import com.neuma573.autoboard.like.model.entity.Like;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LikeRequest {
    private Long postId;

    public static Like toEntity(User user, Post post) {
        return Like.builder()
                .post(post)
                .createdBy(user)
                .build();
    }
}
