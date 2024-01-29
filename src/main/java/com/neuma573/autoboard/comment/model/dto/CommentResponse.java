package com.neuma573.autoboard.comment.model.dto;

import com.neuma573.autoboard.comment.model.entity.Comment;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentResponse {

    private Long id;
    private String content;
    private UserResponse createdBy;
    private String createdAt;

    private boolean isDeleted;

    private CommentResponse parent;


    public static CommentResponse of(Comment comment) {

        CommentResponse parentResponse = comment.getParentComment() != null
                ? CommentResponse.of(comment.getParentComment())
                : null;

        UserResponse userResponse = UserResponse.builder()
                .id(comment.getCreatedBy().getId())
                .email(comment.getCreatedBy().getEmail())
                .name(comment.getCreatedBy().getName())
                .build();

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdBy(userResponse)
                .createdAt(comment.getFormattedCreatedAt())
                .parent(parentResponse)
                .isDeleted(comment.isDeleted())
                .build();

    }
}
