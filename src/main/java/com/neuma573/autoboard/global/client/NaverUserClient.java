package com.neuma573.autoboard.global.client;

import com.neuma573.autoboard.user.model.dto.NaverUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "NaverUserClient", url = "https://openapi.naver.com")
public interface NaverUserClient {
    @GetMapping("/v1/nid/me")
    NaverUserResponse getNaverUserResponse(@RequestHeader("Authorization") String token);
}
