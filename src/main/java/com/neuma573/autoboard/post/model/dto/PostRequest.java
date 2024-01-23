package com.neuma573.autoboard.post.model.dto;

import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.user.model.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostRequest {

    @NotBlank(message = "제목은 비워 둘 수 없습니다")
    @Size(min = 1, max = 80, message = "제목은 1자 이상 80자 이하이어야 합니다")
    private String title;

    @NotEmpty(message = "내용은 비워 둘 수 없습니다")
    @Size(min = 1, max = 1000, message = "내용은 1자 이상 1000자 이하이어야 합니다")
    private String content;

    @NotNull
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
