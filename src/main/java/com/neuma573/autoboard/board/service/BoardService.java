package com.neuma573.autoboard.board.service;

import com.neuma573.autoboard.board.model.dto.BoardRequest;
import com.neuma573.autoboard.board.model.dto.BoardResponse;
import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.board.repository.BoardRepository;
import com.neuma573.autoboard.global.exception.AccessDeniedException;
import com.neuma573.autoboard.global.exception.BoardNotAccessibleException;
import com.neuma573.autoboard.global.exception.BoardNotFoundException;
import com.neuma573.autoboard.global.exception.UserNotFoundException;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    @Transactional
    public List<BoardResponse> getBoardList(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        List<Board> boards;

        if (userOptional.isPresent() && userOptional.get().isAdmin()) {
            Iterable<Board> boardIterable = boardRepository.findAll();
            boards = StreamSupport.stream(boardIterable.spliterator(), false)
                    .collect(Collectors.toList());
            return boards.stream()
                    .map(BoardResponse::of)
                    .collect(Collectors.toList());

        } else {
            boards = boardRepository.findPublicAndNotDeletedBoardWith(userOptional);

            return boards.stream()
                    .map(BoardResponse::ofWithoutDeleted)
                    .collect(Collectors.toList());
        }


    }

    @Transactional
    public BoardResponse saveBoard(Long id, BoardRequest boardRequest) {
        User user = Optional.ofNullable(id)
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (!user.isAdmin()) {
            throw new AccessDeniedException();
        }

        return BoardResponse.of(boardRepository.save(
                boardRequest.toEntity(
                user
                )
        ));
    }

    @Transactional
    public boolean checkAccessible(Long boardId, Long userId) {
        Board board  = boardRepository.findById(boardId).orElseThrow(() -> new BoardNotFoundException("존재하지 않는 게시판입니다."));
        if (userId == -1L) {
            return board.isPublic();
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));

        return board.isAccessible(user);
    }

    @Transactional
    public BoardResponse getBoardInfo(Long boardId) {
        return BoardResponse.of(boardRepository.findById(boardId).orElseThrow(() -> new BoardNotFoundException("존재하지 않는 게시판입니다.")));
    }

    @Transactional
    public void checkAccessibleAndThrow(Long boardId, Long userId) {
        if (userId != -1L && !checkAccessible(boardId, userId)) {
            throw new BoardNotAccessibleException("접근할 수 없는 게시판입니다.");
        }

        if (!checkAccessible(boardId, userId)) {
            throw new BoardNotAccessibleException("접근할 수 없는 게시판입니다.");
        }
    }

}
