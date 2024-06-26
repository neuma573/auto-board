package com.neuma573.autoboard.post.model.dto;

import com.neuma573.autoboard.post.model.entity.Post;
import lombok.*;

@Builder
@Getter
public class PostResponse {

    private Long id;

    private String createdBy;

    private String title;

    @Setter
    private String content;

    private Long views;

    private boolean isDeleted;

    private String createdAt;

    private Long commentCount;

    private Long likeCount;

    public static PostResponse of(Post post) {
       return PostResponse.builder()
                .id(post.getId())
                .createdBy(post.getCreatedBy().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .views(post.getViews())
                .isDeleted(post.isDeleted())
                .createdAt(post.getFormattedCreatedAt())
                .commentCount(post.getActiveCommentCount())
                .likeCount(post.getLikeCount())
                .build();
    }
}
