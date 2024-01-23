package com.neuma573.autoboard.board.model.dto;

import com.neuma573.autoboard.board.model.entity.Board;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Builder
@Getter
public class BoardResponse {

    private Long id;

    private String boardName;

    private Long postCount;

    private boolean isPublic;


    public static BoardResponse of(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .boardName(board.getName())
                .postCount(Optional.ofNullable(board.getPosts()).map(posts -> (long) posts.size()).orElse(0L))
                .isPublic(board.isPublic())
                .build();
    }
}
