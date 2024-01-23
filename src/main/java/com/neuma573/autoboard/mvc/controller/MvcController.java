package com.neuma573.autoboard.mvc.controller;

import com.neuma573.autoboard.board.model.dto.BoardResponse;
import com.neuma573.autoboard.board.service.BoardService;
import com.neuma573.autoboard.post.service.PostService;
import com.neuma573.autoboard.security.utils.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
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

    private final JwtProvider jwtProvider;

    private final BoardService boardService;

    private final PostService postService;

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

    @GetMapping("/write")
    public ModelAndView showWriteForm(@RequestParam(name = "boardId") Long boardId, HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.getUserId(httpServletRequest);
        if(boardService.checkAccessible(boardId, userId)) {
            ModelAndView modelAndView = new ModelAndView("write");
            modelAndView.addObject("boardInfo", boardService.getBoardInfo(boardId));
            modelAndView.addObject("mode", "write");
            return modelAndView;
        } else {
            return new ModelAndView("error/error");
        }
    }

    @GetMapping("/modify")
    public ModelAndView showModifyForm(@RequestParam(name = "postId") Long postId, HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.getUserId(httpServletRequest);

        if(postService.checkAccessible(postId, userId)) {
            ModelAndView modelAndView = new ModelAndView("write");
            modelAndView.addObject("boardInfo", BoardResponse.builder().boardName("글 수정하기").build());
            modelAndView.addObject("mode", "modify");
            return modelAndView;
        } else {
            return new ModelAndView("error/error");
        }
    }

    @GetMapping("/post")
    public ModelAndView showPost() {
        return new ModelAndView("post");
    }

}
