package com.neuma573.autoboard.user.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    KAKAO("Kakao"),
    GOOGLE("Google"),
    APPLE("Apple"),
    NAVER("Naver");

    private final String attribute;
}