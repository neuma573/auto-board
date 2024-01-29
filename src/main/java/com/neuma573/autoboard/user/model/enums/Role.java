package com.neuma573.autoboard.user.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {
    USER("USER"),
    VIP("VIP"),
    BOARD_ADMIN("BOARD_ADMIN"),
    ADMIN("ADMIN");

    private final String description;
}
