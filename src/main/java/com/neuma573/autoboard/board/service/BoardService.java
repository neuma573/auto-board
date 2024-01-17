package com.neuma573.autoboard.board.service;

import com.neuma573.autoboard.board.model.dto.BoardResponse;
import com.neuma573.autoboard.board.repository.BoardRepository;
import com.neuma573.autoboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    public List<BoardResponse> getBoardList(String email) {
        return boardRepository.findPublicAndNotDeletedBoardWith(userRepository.findByEmail(email))
                .stream()
                .map(BoardResponse::of).collect(Collectors.toList());
    }
}
