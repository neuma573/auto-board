package com.neuma573.autoboard.user.model.dto;

import com.neuma573.autoboard.user.validation.annotation.ValidEmailDomain;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {

    @ValidEmailDomain(allowedDomains = {"gmail.com", "naver.com", "nate.com", "kakao.com"}, message = "허용되는 이메일 도메인을 사용해주세요 : \"gmail.com\", \"naver.com\", \"nate.com\", \"kakao.com\"")
    @NotBlank(message = "이메일은 비어 있을 수 없습니다.")
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    private String email;

}