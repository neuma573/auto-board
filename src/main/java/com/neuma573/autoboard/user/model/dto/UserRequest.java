package com.neuma573.autoboard.user.model.dto;

import com.neuma573.autoboard.user.model.enums.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserRequest {

    @NotEmpty(message = "아이디는 비어있을 수 없습니다.")
    private String loginId;

    @NotEmpty(message = "이름은 비어있을 수 없습니다.")
    private String name;

    @NotEmpty(message = "패스워드는 비어있을 수 없습니다.")
    private String password;

}
