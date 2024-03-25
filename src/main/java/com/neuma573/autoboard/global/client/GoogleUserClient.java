package com.neuma573.autoboard.global.client;

import com.neuma573.autoboard.user.model.dto.GoogleUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "GoogleUserClient", url = "https://www.googleapis.com")
public interface GoogleUserClient {
    @GetMapping("/oauth2/v2/userinfo")
    GoogleUserResponse getGoogleUserResponse(@RequestHeader("Authorization") String token);
}
