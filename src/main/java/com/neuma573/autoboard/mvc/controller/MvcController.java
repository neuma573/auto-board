package com.neuma573.autoboard.mvc.controller;

import com.neuma573.autoboard.board.model.annotation.CheckBoardAccess;
import com.neuma573.autoboard.board.model.dto.BoardResponse;
import com.neuma573.autoboard.board.model.enums.BoardAction;
import com.neuma573.autoboard.board.service.BoardService;
import com.neuma573.autoboard.global.utils.RequestUtils;
import com.neuma573.autoboard.policy.service.PolicyService;
import com.neuma573.autoboard.post.model.annotation.CheckPostAccess;
import com.neuma573.autoboard.post.model.dto.PostResponse;
import com.neuma573.autoboard.post.model.enums.PostAction;
import com.neuma573.autoboard.post.service.PostService;
import com.neuma573.autoboard.security.utils.JwtProvider;
import com.neuma573.autoboard.user.model.dto.UserResponse;
import com.neuma573.autoboard.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MvcController {

    private final BoardService boardService;

    private final UserService userService;

    private final JwtProvider jwtProvider;

    private final PostService postService;

    private final PolicyService policyService;

    @Value("${app.oauth2.naver.client-id}")
    private String naverOAuthClientId;

    @Value("${app.oauth2.google.client-id}")
    private String googleOAuthClientId;

    @Value("${app.domain}")
    private String domain;

    @GetMapping("/login")
    public ModelAndView showLoginForm(@RequestParam(value = "redirect", required = false) String redirectUrl) {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("naverClientId", naverOAuthClientId);
        modelAndView.addObject("googleClientId", googleOAuthClientId);
        modelAndView.addObject("domain", domain);
        modelAndView.addObject("state", redirectUrl != null ? redirectUrl : '/');

        return modelAndView;
    }

    @GetMapping(value = {"/main", "/"})
    public ModelAndView showMainPage() {
        return new ModelAndView("main");
    }

    @GetMapping("/signup-options")
    public ModelAndView showSignupOptions() {
        ModelAndView modelAndView = new ModelAndView("signup-options");

        modelAndView.addObject("naverClientId", naverOAuthClientId);
        modelAndView.addObject("googleClientId", googleOAuthClientId);
        modelAndView.addObject("domain", domain);
        modelAndView.addObject("state", "/");

        return modelAndView;
    }

    @GetMapping("/join")
    public ModelAndView showJoin() {
        ModelAndView modelAndView = new ModelAndView("join");
        modelAndView.addObject("consentPolicy", policyService.getConsentPolicy());
        modelAndView.addObject("termOfUse", policyService.getTermOfUse());

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

    @CheckPostAccess(action = PostAction.READ)
    @CheckBoardAccess(action = BoardAction.READ)
    @GetMapping("/post")
    public ModelAndView showPost(@RequestParam(name = "postId") Long postId, HttpServletRequest httpServletRequest) {
        ModelAndView modelAndView = new ModelAndView("post");
        String ipAddress = RequestUtils.getClientIpAddress(httpServletRequest);
        PostResponse postResponse = postService.getPost(ipAddress, postId);
        modelAndView.addObject("postResponse", postResponse);
        modelAndView.addObject("postId", postId);
        return modelAndView;
    }

    @GetMapping("/mypage")
    public ModelAndView showMypage(HttpServletRequest httpServletRequest) {
        Long userId = jwtProvider.parseUserId(httpServletRequest);
        ModelAndView modelAndView = new ModelAndView("mypage");
        UserResponse userResponse = UserResponse.of(userService.getUserById(userId));
        modelAndView.addObject("userResponse", userResponse);
        return modelAndView;
    }

    @GetMapping("/oauth-redirect")
    public ModelAndView oauthRedirect(
            @RequestParam(name = "token") String token,
            @RequestParam(name = "email") String email,
            @RequestParam(value = "redirect", required = false) String redirectUrl
    ) {
        ModelAndView modelAndView = new ModelAndView("oauth_redirect");
        modelAndView.addObject("token", token);
        modelAndView.addObject("email", email);
        if (redirectUrl != null) {
            modelAndView.addObject("redirectUrl", redirectUrl);
        }
        return modelAndView;
    }

}
