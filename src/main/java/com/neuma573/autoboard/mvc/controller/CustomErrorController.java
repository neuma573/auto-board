package com.neuma573.autoboard.mvc.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest httpServletRequest) {
        ModelAndView modelAndView = new ModelAndView("error/error");

        Object statusObject = httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (statusObject != null) {
            int statusCode = Integer.parseInt(statusObject.toString());
            status = HttpStatus.valueOf(statusCode);
        }

        modelAndView.addObject("code", status.value());
        modelAndView.addObject("message", status.getReasonPhrase());
        return modelAndView;
    }
}
