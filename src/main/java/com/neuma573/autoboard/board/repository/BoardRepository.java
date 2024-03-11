package com.neuma573.autoboard.board.repository;

import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.user.model.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface BoardRepository extends CrudRepository<Board, Long>, BoardCustomRepository, RevisionRepository<Board, Long, Long> {

    Optional<Board> findByUsersAndId(User user, Long id);

}
