package com.neuma573.autoboard.post.model.dto;

import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.user.model.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostRequest {

    @NotBlank
    @Size(min=1, max=80)
    private String title;

    @NotEmpty
    @Size(min=1, max=1000)
    private String content;

    @NotEmpty
    private Long boardId;

    public Post of(Board board, User user) {
        return Post.builder()
                .board(board)
                .title(title)
                .content(content)
                .createdBy(user)
                .isDeleted(false)
                .views(0L)
                .build();
    }
}
