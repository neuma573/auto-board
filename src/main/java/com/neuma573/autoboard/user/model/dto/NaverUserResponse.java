package com.neuma573.autoboard.user.model.dto;

import lombok.Getter;

@Getter
public class NaverUserResponse {

    private String resultcode;
    private String message;
    private Response response;

    @Getter
    public static class Response {

        private String id;
        private String nickname;
        private String email;

    }

    public String getId() {
        return getResponse().getId();
    }

    public String getEmail() {
        return getResponse().getEmail();
    }

}
