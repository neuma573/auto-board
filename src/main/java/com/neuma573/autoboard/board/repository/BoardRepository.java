package com.neuma573.autoboard.board.repository;

import com.neuma573.autoboard.board.model.entity.Board;
import org.springframework.data.repository.CrudRepository;

public interface BoardRepository extends CrudRepository<Board, Long> {
}
