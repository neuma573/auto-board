package com.neuma573.autoboard.post.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostModifyRequest {

    private Long postId;

    @NotBlank(message = "제목은 비워 둘 수 없습니다")
    @Size(min = 1, max = 80, message = "제목은 1자 이상 80자 이하이어야 합니다")
    private String title;

    @NotEmpty(message = "내용은 비워 둘 수 없습니다")
    @Size(min = 1, max = 1000, message = "내용은 1자 이상 1000자 이하이어야 합니다")
    private String content;
}
