package com.neuma573.autoboard.user.model.dto;

import com.neuma573.autoboard.global.model.enums.Status;
import com.neuma573.autoboard.user.model.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

@Getter
@Builder
@AllArgsConstructor
public class UserRequest {

    @Email(message = "email 양식을 맞춰주세요.")
    @NotEmpty(message = "이메일은 비어있을 수 없습니다.")
    private String email;

    @NotEmpty(message = "이름은 비어있을 수 없습니다.")
    private String name;

    @NotEmpty(message = "패스워드는 비어있을 수 없습니다.")
    @Setter
    private String password;

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .name(this.name)
                .password(this.password)
                .status(Status.INACTIVE)
                .roles(new HashSet<>())
                .failCount(0L)
                .build();
    }
}
