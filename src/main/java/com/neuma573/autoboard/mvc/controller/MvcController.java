package com.neuma573.autoboard.mvc.controller;

import com.neuma573.autoboard.board.service.BoardService;
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
        String email = jwtProvider.parseEmailWithValidation(httpServletRequest);
        if(boardService.checkAccessible(boardId, email)) {
            ModelAndView modelAndView = new ModelAndView("write");
            modelAndView.addObject("boardInfo", boardService.getBoardInfo(boardId));
            return modelAndView;
        } else {
            return new ModelAndView("error/error");
        }

    }

}
