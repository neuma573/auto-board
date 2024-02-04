package com.neuma573.autoboard.comment.model.dto;

import com.neuma573.autoboard.comment.model.entity.Comment;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.user.model.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentRequest {

    @NotNull(message = "잘못된 요청입니다")
    private Long postId;

    private Long parentId;

    @NotBlank(message = "댓글 본문은 비어 있을 수 없습니다")
    @Size(min = 1, max = 300, message = "내용은 1자 이상 300자 이하이어야 합니다")
    private String content;

    public Comment toEntity(Post post, User user, Comment parentComment) {
        return Comment.builder()
                .content(content)
                .post(post)
                .createdBy(user)
                .parentComment(parentComment)
                .build();
    }
}
