package com.neuma573.autoboard.board.controller;

import com.neuma573.autoboard.board.model.annotation.CheckBoardAccess;
import com.neuma573.autoboard.board.model.dto.BoardRequest;
import com.neuma573.autoboard.board.model.dto.BoardResponse;
import com.neuma573.autoboard.board.model.enums.BoardAction;
import com.neuma573.autoboard.board.service.BoardService;
import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.security.utils.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@Slf4j
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;

    private final JwtProvider jwtProvider;

    private final ResponseUtils responseUtils;

    @GetMapping("")
    public ResponseEntity<Response<List<BoardResponse>>> getBoardList(HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.parseUserIdSafely(httpServletRequest);
        return ResponseEntity.ok().body(responseUtils.success(boardService.getBoardList(userId)));
    }

    @CheckBoardAccess(action = BoardAction.CREATE)
    @PostMapping("")
    public ResponseEntity<Response<BoardResponse>> saveBoard(@Valid @RequestBody BoardRequest boardRequest, HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.parseUserId(httpServletRequest);
        return ResponseEntity.created(URI.create("/main")).body(responseUtils.created(boardService.generateBoard(userId, boardRequest)));
    }

    @CheckBoardAccess(action = BoardAction.UPDATE)
    @PutMapping("")
    public ResponseEntity<Void> modifyBoard(@Valid @RequestBody BoardRequest boardRequest) {
        boardService.modifyBoard(boardRequest);
        return ResponseEntity.noContent().build();
    }

    @CheckBoardAccess(action = BoardAction.DELETE)
    @DeleteMapping("")
    public ResponseEntity<Void> deleteBoard(@RequestParam(name = "boardId") Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }
}
