package com.neuma573.autoboard.user.model.dto;

import com.neuma573.autoboard.global.model.enums.Status;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.model.entity.UserRole;
import com.neuma573.autoboard.user.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Builder
@AllArgsConstructor
public class UserRequest {

    @Email(message = "email 양식을 맞춰주세요.")
    @NotEmpty(message = "이메일은 비어있을 수 없습니다.")
    private String email;

    @NotEmpty(message = "이름은 비어있을 수 없습니다.")
    private String name;

    @Size(min = 8, message = "패스워드는 최소 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+])[a-zA-Z0-9!@#$%^&*()_+]{8,}$", message = "패스워드는 숫자, 문자, 특수문자를 포함해야 합니다.")
    @NotEmpty(message = "패스워드는 비어있을 수 없습니다.")
    @Setter
    private String password;

    public User toEntity() {
        Set<UserRole> initialRoles = ConcurrentHashMap.newKeySet();
        return User.builder()
                .email(this.email)
                .name(this.name)
                .password(this.password)
                .status(Status.INACTIVE)
                .roles(initialRoles)
                .failCount(0L)
                .build();
    }
}
