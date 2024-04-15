package com.neuma573.autoboard.user.model.dto;

import com.neuma573.autoboard.user.model.enums.AuthenticationProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProviderUserResponse {

    String providerId;

    String email;

    AuthenticationProviderType authenticationProviderType;

}
