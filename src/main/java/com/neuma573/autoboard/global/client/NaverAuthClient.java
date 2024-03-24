package com.neuma573.autoboard.global.client;

import com.neuma573.autoboard.user.model.dto.NaverTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "NaverAuthClient", url = "https://nid.naver.com")
public interface NaverAuthClient {
    @GetMapping("/oauth2.0/token")
    NaverTokenResponse getToken(@RequestParam("grant_type") String grantType,
                                @RequestParam("client_id") String clientId,
                                @RequestParam("client_secret") String clientSecret,
                                @RequestParam("code") String code,
                                @RequestParam("state") String state);
}
