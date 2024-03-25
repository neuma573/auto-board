package com.neuma573.autoboard.global.client;

import com.neuma573.autoboard.user.model.dto.GoogleTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "GoogleAuthClient", url = "https://oauth2.googleapis.com")
public interface GoogleAuthClient {

    @PostMapping("/token")
    GoogleTokenResponse getToken(@RequestParam("code") String grantType,
                                 @RequestParam("client_id") String clientId,
                                 @RequestParam("client_secret") String clientSecret,
                                 @RequestParam("redirect_uri") String redirect_uri,
                                 @RequestParam("grant_type") String state);

}
