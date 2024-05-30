package com.neuma573.autoboard.like.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LikeResponse {

    private Long postId;

    private Long likeCount;

    public static LikeResponse of(Long postId, Long likeCount) {
        return LikeResponse.builder()
                .postId(postId)
                .likeCount(likeCount)
                .build();
    }
}
