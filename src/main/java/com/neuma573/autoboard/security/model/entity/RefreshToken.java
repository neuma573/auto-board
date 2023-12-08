package com.neuma573.autoboard.security.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@RedisHash("RefreshToken")
public class RefreshToken {

    @Id
    private String loginId;

    @Setter
    private String token;

}