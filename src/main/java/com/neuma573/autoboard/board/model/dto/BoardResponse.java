package com.neuma573.autoboard.board.model.dto;

import com.neuma573.autoboard.board.model.entity.Board;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Builder
@Getter
public class BoardResponse {

    private Long id;

    private String name;

    private Long postCount;

    private boolean isPublic;


    public static BoardResponse of(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .name(board.getName())
                .postCount(Optional.ofNullable(board.getPosts()).map(posts -> (long) posts.size()).orElse(0L))
                .isPublic(board.isPublic())
                .build();
    }

    public static BoardResponse ofWithoutDeleted(Board board) {
        long countNotDeletedPosts = Optional.ofNullable(board.getPosts())
                .map(posts -> posts.stream()
                        .filter(post -> !post.isDeleted())
                        .count())
                .orElse(0L);

        return BoardResponse.builder()
                .id(board.getId())
                .name(board.getName())
                .postCount(countNotDeletedPosts)
                .isPublic(board.isPublic())
                .build();
    }

}
