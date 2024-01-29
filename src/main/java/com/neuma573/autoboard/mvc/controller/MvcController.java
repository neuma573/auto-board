package com.neuma573.autoboard.mvc.controller;

import com.neuma573.autoboard.board.model.annotation.CheckBoardAccess;
import com.neuma573.autoboard.board.model.dto.BoardResponse;
import com.neuma573.autoboard.board.model.enums.BoardAction;
import com.neuma573.autoboard.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MvcController {

    private final BoardService boardService;

    @GetMapping("/login")
    public ModelAndView showLoginForm() {
        return new ModelAndView("login");
    }

    @GetMapping(value = {"/main", "/"})
    public ModelAndView showMainPage() {
        return new ModelAndView("main");
    }

    @GetMapping("/join")
    public ModelAndView showJoin() {
        return new ModelAndView("join");
    }

    @CheckBoardAccess(action = BoardAction.READ)
    @GetMapping("/write")
    public ModelAndView showWriteForm(@RequestParam(name = "boardId") Long boardId) {
        ModelAndView modelAndView = new ModelAndView("write");
        modelAndView.addObject("boardInfo", boardService.getBoardInfo(boardId));
        modelAndView.addObject("mode", "write");
        return modelAndView;

    }

    @CheckBoardAccess(action = BoardAction.READ)
    @GetMapping("/modify")
    public ModelAndView showModifyForm(@RequestParam(name = "postId") Long postId) {
        ModelAndView modelAndView = new ModelAndView("write");
        modelAndView.addObject("boardInfo", BoardResponse.builder().name("글 수정하기").build());
        modelAndView.addObject("mode", "modify");
        return modelAndView;
    }

    @GetMapping("/post")
    public ModelAndView showPost(@RequestParam(name = "postId") Long postId ) {
        ModelAndView modelAndView = new ModelAndView("post");
        modelAndView.addObject("postId", postId);
        return modelAndView;
    }

}
