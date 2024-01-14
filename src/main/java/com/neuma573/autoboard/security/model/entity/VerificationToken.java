package com.neuma573.autoboard.security.model.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Builder
@RedisHash("VerificationToken")
public class VerificationToken {

    @Id
    private String id;

    @Indexed
    @Getter
    private String token;

    @Getter
    private String email;

    @Getter
    private LocalDateTime expiryDate;

}
