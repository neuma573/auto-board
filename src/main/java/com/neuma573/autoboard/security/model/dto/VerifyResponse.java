package com.neuma573.autoboard.security.model.dto;

import com.neuma573.autoboard.user.model.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
@Getter
public class VerifyResponse {

    private boolean isValid;
    private Set<Role> userRole;

}
