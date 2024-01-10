package com.neuma573.autoboard.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MvcController {

    @GetMapping("/login")
    public ModelAndView showLoginForm() {
        return new ModelAndView("login");
    }

    @GetMapping(value = {"/main", "/"})
    public ModelAndView showMainPage() {
        return new ModelAndView("main");
    }

}
