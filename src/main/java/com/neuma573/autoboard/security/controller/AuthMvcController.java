package com.neuma573.autoboard.security.controller;

import com.neuma573.autoboard.user.model.dto.ProviderUserResponse;
import com.neuma573.autoboard.user.service.OAuthService;
import com.neuma573.autoboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Controller
public class AuthMvcController {

    private final UserService userService;

    private final OAuthService oAuthService;

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        if (userService.activateUserAccount(token)) {

            redirectAttributes.addFlashAttribute("message", "계정이 성공적으로 활성화 되었습니다.");
        } else {

            redirectAttributes.addFlashAttribute("message", "유효하지 않거나 만료된 활성화 토큰입니다.");
        }
        return "redirect:/login";
    }

    @GetMapping("/oauth")
    public ModelAndView showOAuthJoin(@RequestParam(value = "code") String uuid, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView("oauth_join");
        ProviderUserResponse providerUserResponse = oAuthService.getUserByUuid(uuid);

        if (providerUserResponse == null) {
            modelAndView.setViewName("redirect:/login");
            redirectAttributes.addFlashAttribute("message", "유효하지 않은 소셜 가입정보입니다. 다시 가입을 시도해주세요.");
        } else {
            modelAndView.setViewName("oauth_join");
            modelAndView.addObject("email", providerUserResponse.getEmail());
            modelAndView.addObject("uuid", uuid);
        }


        return modelAndView;
    }
}
