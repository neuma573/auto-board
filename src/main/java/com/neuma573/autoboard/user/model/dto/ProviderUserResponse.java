package com.neuma573.autoboard.user.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProviderUserResponse {

    String providerId;

    String email;

}
