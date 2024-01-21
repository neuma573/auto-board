package com.neuma573.autoboard.board.service;

import com.neuma573.autoboard.board.model.dto.BoardRequest;
import com.neuma573.autoboard.board.model.dto.BoardResponse;
import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.board.repository.BoardRepository;
import com.neuma573.autoboard.global.exception.BoardNotFoundException;
import com.neuma573.autoboard.global.exception.UserNotFoundException;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    @Transactional
    public List<BoardResponse> getBoardList(String email) {
        return boardRepository.findPublicAndNotDeletedBoardWith(userRepository.findByEmail(email))
                .stream()
                .map(BoardResponse::of).collect(Collectors.toList());
    }

    @Transactional
    public BoardResponse saveBoard(String email, BoardRequest boardRequest) {
        return BoardResponse.of(boardRepository.save(boardRequest.toEntity(
                userRepository.findByEmail(email).orElseThrow()
        )));
    }

    @Transactional()
    public boolean checkAccessible(Long boardId, String email) {
        Board board  = boardRepository.findById(boardId).orElseThrow(() -> new BoardNotFoundException("존재하지 않는 게시판입니다."));

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));

        return board.isAccessible(user);
    }

    @Transactional
    public BoardResponse getBoardInfo(Long boardId) {
        return BoardResponse.of(boardRepository.findById(boardId).orElseThrow(() -> new BoardNotFoundException("존재하지 않는 게시판입니다.")));
    }

}
