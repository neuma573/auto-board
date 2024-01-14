package com.neuma573.autoboard.security.controller;

import com.neuma573.autoboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Controller
public class AuthMvcController {

    private final UserService userService;

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        if (userService.activateUserAccount(token)) {

            redirectAttributes.addFlashAttribute("successMessage", "Your account has been successfully activated.");
            return "redirect:/login";
        } else {

            redirectAttributes.addFlashAttribute("errorMessage", "Invalid or expired activation token.");
            return "redirect:/error";
        }
    }
}
