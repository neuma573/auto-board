package com.neuma573.autoboard.mvc.controller;

import com.neuma573.autoboard.board.model.annotation.CheckBoardAccess;
import com.neuma573.autoboard.board.model.dto.BoardResponse;
import com.neuma573.autoboard.board.model.enums.BoardAction;
import com.neuma573.autoboard.board.service.BoardService;
import com.neuma573.autoboard.user.model.dto.ProviderUserResponse;
import com.neuma573.autoboard.user.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MvcController {

    private final BoardService boardService;

    private final OAuthService oAuthService;

    @Value("${app.oauth2.naver.client-id}")
    private String naverOAuthClientId;

    @Value("${app.oauth2.google.client-id}")
    private String googleOAuthClientId;

    @Value("${app.domain}")
    private String domain;

    @GetMapping("/login")
    public ModelAndView showLoginForm() {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("naverClientId", naverOAuthClientId);
        modelAndView.addObject("googleClientId", googleOAuthClientId);
        modelAndView.addObject("domain", domain);
        modelAndView.addObject("state", URLEncoder.encode(UUID.randomUUID().toString(), StandardCharsets.UTF_8));

        return modelAndView;
    }

    @GetMapping(value = {"/main", "/"})
    public ModelAndView showMainPage() {
        return new ModelAndView("main");
    }

    @GetMapping("/join")
    public ModelAndView showJoin() {
        return new ModelAndView("join");
    }

    @GetMapping("/oauth/join")
    public ModelAndView showOAuthJoin( @RequestParam(value = "code") String uuid) {
        ModelAndView modelAndView = new ModelAndView("/oauth_join");
        ProviderUserResponse providerUserResponse = oAuthService.getUserByUuid(uuid);
        modelAndView.addObject("email", providerUserResponse.getEmail());
        modelAndView.addObject("uuid", uuid);


        return modelAndView;
    }

    @CheckBoardAccess(action = BoardAction.READ)
    @GetMapping("/write")
    public ModelAndView showWriteForm(@RequestParam(name = "boardId") Long boardId) {
        ModelAndView modelAndView = new ModelAndView("write");
        modelAndView.addObject("boardInfo", boardService.getBoardInfo(boardId));
        modelAndView.addObject("mode", "write");
        modelAndView.addObject("tempId", UUID.randomUUID());
        return modelAndView;
    }

    @CheckBoardAccess(action = BoardAction.READ)
    @GetMapping("/modify")
    public ModelAndView showModifyForm() {
        ModelAndView modelAndView = new ModelAndView("write");
        modelAndView.addObject("boardInfo", BoardResponse.builder().name("글 수정하기").build());
        modelAndView.addObject("mode", "modify");
        modelAndView.addObject("tempId", UUID.randomUUID());
        return modelAndView;
    }

    @GetMapping("/post")
    public ModelAndView showPost(@RequestParam(name = "postId") Long postId ) {
        ModelAndView modelAndView = new ModelAndView("post");
        modelAndView.addObject("postId", postId);
        return modelAndView;
    }

}
