package com.neuma573.autoboard.post.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostModifyRequest extends PostRequest {

    private Long postId;
}
