package com.neuma573.autoboard.board.model.dto;

import com.neuma573.autoboard.board.model.entity.Board;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BoardResponse {

    private Long id;

    private String boardName;

    private Long postCount;


    public static BoardResponse of(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .boardName(board.getName())
                .postCount((long) board.getPosts().size())
                .build();
    }
}
