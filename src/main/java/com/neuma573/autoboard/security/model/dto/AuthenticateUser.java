package com.neuma573.autoboard.security.model.dto;

import com.neuma573.autoboard.user.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class AuthenticateUser {
    private String loginId;
    private Set<Role> roles;
}