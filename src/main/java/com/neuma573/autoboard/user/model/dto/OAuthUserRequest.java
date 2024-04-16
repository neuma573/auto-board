package com.neuma573.autoboard.user.model.dto;

import com.neuma573.autoboard.global.model.enums.Status;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.model.entity.UserRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class OAuthUserRequest {

    @Size(min = 2, max = 6, message = "이름은 최소 2글자, 최대 6글자까지 가능합니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "이름에 특수문자를 사용할 수 없습니다.")
    @NotEmpty(message = "이름은 비어있을 수 없습니다.")
    private String name;

    private String uuid;


    public User toEntity(ProviderUserResponse providerUserResponse) {
        Set<UserRole> initialRoles = ConcurrentHashMap.newKeySet();
        return User.builder()
                .email(providerUserResponse.getEmail())
                .name(this.name)
                .password(UUID.randomUUID().toString())
                .status(Status.ACTIVE)
                .roles(initialRoles)
                .failCount(0L)
                .build();
    }
}
