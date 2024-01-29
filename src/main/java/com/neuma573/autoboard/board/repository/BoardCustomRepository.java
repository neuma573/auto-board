package com.neuma573.autoboard.board.repository;

import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.user.model.entity.User;

import java.util.List;

public interface BoardCustomRepository {

    List<Board> findPublicAndNotDeletedBoardWith(User user);

}
