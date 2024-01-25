package com.neuma573.autoboard.board.service;

import com.neuma573.autoboard.board.model.dto.BoardRequest;
import com.neuma573.autoboard.board.model.dto.BoardResponse;
import com.neuma573.autoboard.board.model.entity.Board;
import com.neuma573.autoboard.board.model.enums.BoardAction;
import com.neuma573.autoboard.board.repository.BoardRepository;
import com.neuma573.autoboard.global.exception.BoardNotFoundException;
import com.neuma573.autoboard.security.model.annotation.CheckPermission;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.model.enums.Role;
import com.neuma573.autoboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    private final UserService userService;

    @Transactional
    public List<BoardResponse> getBoardList(Long userId) {

        User user = userService.getUserByIdSafely(userId);
        if (user != null && userService.isAdmin(user)) {
            return getBoardListForAdmin();
        } else {
            return getBoardListForUser(user);
        }
    }

    @CheckPermission(role = Role.ADMIN)
    public List<BoardResponse> getBoardListForAdmin() {
        Iterable<Board> boardIterable = boardRepository.findAll();
        List<Board> boards = StreamSupport.stream(boardIterable.spliterator(), false)
                .toList();
        return boards.stream()
                .map(BoardResponse::of)
                .collect(Collectors.toList());
    }


    public List<BoardResponse> getBoardListForUser(User user) {
        List<Board> boards = boardRepository.findPublicAndNotDeletedBoardWith(user);

        return boards.stream()
                .map(BoardResponse::ofWithoutDeleted)
                .collect(Collectors.toList());
    }


    @Transactional
    public BoardResponse saveBoard(Long userId, BoardRequest boardRequest) {
        User user = userService.getUserById(userId);
        return BoardResponse.of(boardRepository.save(
                boardRequest.toEntity(
                user)
        ));
    }

    @Transactional
    public BoardResponse getBoardInfo(Long boardId) {
        return BoardResponse.of(getBoardById(boardId));
    }

    @Transactional
    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new BoardNotFoundException("존재하지 않는 게시판입니다."));
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        getBoardById(boardId).delete();
    }

    @Transactional
    public void modifyBoard(BoardRequest boardRequest) {
        Board board = getBoardById(boardRequest.getId());
        modify(board, boardRequest);
    }

    public void modify(Board board, BoardRequest boardRequest) {
        board.setName(boardRequest.getName());
    }

    public boolean isContainedUser(User user, Long boardId) {
        return boardRepository.findByUsersAndId(user, boardId).isPresent();
    }

    @Transactional
    public boolean isCreatable(Long userId) {
        return userService.isAdmin(
                userService.getUserById(userId)
        );
    }

    @Transactional
    public boolean isBoardAccessible(Long userId, Long boardId, BoardAction action) {
        Board board = getBoardById(boardId);
        User user = userService.getUserByIdSafely(userId);

        return switch (action) {
            case READ -> {
                if (user == null) {
                    yield board.isPublic() && !board.isDeleted();
                }
                yield userService.isAdmin(user) || (!board.isDeleted() && (board.isPublic() || isContainedUser(user, boardId)));
            }
            case DELETE -> userService.isAdmin(user) && !board.isDeleted();
            case UPDATE, CREATE -> userService.isAdmin(user);
            default -> false;
        };
    }

}
