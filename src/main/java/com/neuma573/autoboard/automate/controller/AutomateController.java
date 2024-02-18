package com.neuma573.autoboard.automate.controller;

import com.neuma573.autoboard.automate.service.WhitelistService;
import com.neuma573.autoboard.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
@RestController
public class AutomateController {

    private final WhitelistService whitelistService;

    private final AuthService authService;

    @GetMapping
    public ResponseEntity<?> auth(
            @RequestParam(name = "app-name") String appName,
            HttpServletRequest httpServletRequest) {
        String ip = authService.getClientIpAddress(httpServletRequest);
        log.info("ip : {} ; appName : {}", ip, appName);
        if (whitelistService.isValidIp(
                ip,
                appName)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
