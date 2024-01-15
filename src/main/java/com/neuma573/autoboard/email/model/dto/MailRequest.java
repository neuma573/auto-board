package com.neuma573.autoboard.email.model.dto;

import com.neuma573.autoboard.security.model.entity.VerificationToken;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MailRequest {

    private String to;

    private String name;

    private VerificationToken verificationToken;
}
