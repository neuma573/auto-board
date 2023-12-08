package com.neuma573.autoboard.user.model.dto;

import com.neuma573.autoboard.global.model.enums.Status;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class UserRequest {

    @NotEmpty(message = "아이디는 비어있을 수 없습니다.")
    private String loginId;

    @NotEmpty(message = "이름은 비어있을 수 없습니다.")
    private String name;

    @NotEmpty(message = "패스워드는 비어있을 수 없습니다.")
    @Setter
    private String password;

    @Email(message = "email 양식을 맞춰주세요.")
    private String email;

    public User toEntity() {
        return User.builder()
                .loginId(this.loginId)
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .status(Status.ACTIVE)
                .build();
    }
}
