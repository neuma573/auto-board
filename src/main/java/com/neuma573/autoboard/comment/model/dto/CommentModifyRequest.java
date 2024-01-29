package com.neuma573.autoboard.comment.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CommentModifyRequest extends CommentRequest {

    @NotNull(message = "잘못된 요청입니다")
    private Long commentId;
}
