package com.neuma573.autoboard.post.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class PostModifyRequest extends PostRequest {

    @NotNull
    private Long postId;
}
