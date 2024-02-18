package com.neuma573.autoboard.global.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Status {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    BANNED("Banned"),
    DELETED("Deleted");

    private final String status;

}
