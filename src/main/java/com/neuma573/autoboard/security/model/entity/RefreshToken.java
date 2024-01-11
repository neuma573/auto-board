package com.neuma573.autoboard.security.model.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@RedisHash("RefreshToken")
public class RefreshToken {

    @Id
    private String email;

    @Indexed
    private String token;

}