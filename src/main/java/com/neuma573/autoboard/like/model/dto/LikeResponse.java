package com.neuma573.autoboard.like.model.dto;

import com.neuma573.autoboard.like.model.entity.Like;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class LikeResponse {

    private Long postId;

    private UserResponse userResponse;

    private LocalDateTime createdAt;

    private String message;

    public static LikeResponse of(Like like) {
        return LikeResponse.builder()
                .postId(like.getPost().getId())
                .userResponse(UserResponse.of(like.getCreatedBy()))
                .createdAt(like.getCreatedAt())
                .build();
    }

    public static LikeResponse ofRemovedLike(Long postId, UserResponse userResponse) {
        return LikeResponse.builder()
                .postId(postId)
                .userResponse(userResponse)
                .message("Like removed")
                .build();
    }

}
