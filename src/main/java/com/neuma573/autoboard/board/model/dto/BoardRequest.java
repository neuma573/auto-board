package com.neuma573.autoboard.board.model.dto;

import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.user.model.entity.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class BoardRequest {

    @NotEmpty(message = "게시판 이름은 비어있을 수 없습니다.")
    private String boardName;

    public Board toEntity(User user) {
        Set<User> initialUsers = ConcurrentHashMap.newKeySet();
        initialUsers.add(user);

        return Board.builder()
                .isPublic(false)
                .isDeleted(false)
                .name(boardName)
                .createdBy(user)
                .users(initialUsers)
                .build();
    }

}
