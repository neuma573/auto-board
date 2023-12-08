package com.neuma573.autoboard.security.model.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@RedisHash("RefreshToken")
public class RefreshToken {

    @Id
    private String userId;

    private String token;

}