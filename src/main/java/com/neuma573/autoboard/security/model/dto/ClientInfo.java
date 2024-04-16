package com.neuma573.autoboard.security.model.dto;

import com.neuma573.autoboard.global.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ClientInfo {

    private String clientIpAddress;

    private String userAgent;

    private String requestUri;

    public static ClientInfo of(HttpServletRequest httpServletRequest) {
        return ClientInfo.builder()
                .clientIpAddress(RequestUtils.getClientIpAddress(httpServletRequest))
                .userAgent(RequestUtils.getUserAgent(httpServletRequest))
                .requestUri(RequestUtils.getRequestUri(httpServletRequest))
                .build();
    }

}
