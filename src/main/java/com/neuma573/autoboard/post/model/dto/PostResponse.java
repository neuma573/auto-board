package com.neuma573.autoboard.post.model.dto;

import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import lombok.*;

@Builder
@Getter
public class PostResponse {

    private Long id;

    private UserResponse userResponse;

    private String title;

    @Setter
    private String content;

    private Long views;

    private boolean isDeleted;

    private String createdAt;

    private Long commentCount;

    public static PostResponse of(Post post) {
       return PostResponse.builder()
                .id(post.getId())
                .userResponse(UserResponse.of(post.getCreatedBy()))
                .title(post.getTitle())
                .content(post.getContent())
                .views(post.getViews())
                .isDeleted(post.isDeleted())
                .createdAt(post.getFormattedCreatedAt())
                .commentCount(post.getActiveCommentCount())
                .build();
    }
}
