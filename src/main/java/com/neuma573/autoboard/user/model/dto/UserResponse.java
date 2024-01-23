package com.neuma573.autoboard.user.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String name;
}
