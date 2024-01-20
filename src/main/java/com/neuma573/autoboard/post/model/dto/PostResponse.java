package com.neuma573.autoboard.post.model.dto;

import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostResponse {

    private UserResponse userResponse;

    private String title;

    private String content;

    private Long views;

    private boolean isDeleted;

    public static PostResponse of(Post post) {
       return PostResponse.builder()
                .userResponse(post.getCreatedBy().toResponse())
                .title(post.getTitle())
                .content(post.getContent())
                .views(post.getViews())
                .isDeleted(post.isDeleted())
                .build();
    }
}
