package com.neuma573.autoboard.post.model.dto;

import com.neuma573.autoboard.post.model.entity.Post;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostListResponse {

    private Long id;

    private String userName;

    private String title;

    private Long views;

    private String createdAt;

    private Long commentCount;

    private Long likeCount;

    private boolean hasImages;

    private boolean hasVideo;

    private boolean isDeleted;

    public static PostListResponse of(Post post) {
        return PostListResponse.builder()
                .id(post.getId())
                .userName(post.getCreatedBy().getName())
                .title(post.getTitle())
                .views(post.getViews())
                .createdAt(post.getFormattedCreatedAt())
                .commentCount(post.getActiveCommentCount())
                .likeCount(post.getLikeCount())
                .hasImages(post.hasImage())
                .hasVideo(post.hasVideo())
                .isDeleted(post.isDeleted())
                .build();
    }
}