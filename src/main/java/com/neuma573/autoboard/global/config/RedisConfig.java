package com.neuma573.autoboard.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.neuma573.autoboard.security.model.entity.RefreshToken;
import com.neuma573.autoboard.security.model.entity.VerificationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.username}")
    private String username;

    @Value("${spring.data.redis.password}")
    private String password;

    private final ObjectMapper objectMapper;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        redisStandaloneConfiguration.setUsername(username);
        redisStandaloneConfiguration.setPassword(password);

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, VerificationToken> verificationTokenRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, VerificationToken> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        Jackson2JsonRedisSerializer<VerificationToken> serializer = new Jackson2JsonRedisSerializer<>(VerificationToken.class);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        template.setValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());

        return template;
    }

    @Bean
    public RedisTemplate<String, RefreshToken> refreshTokenRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, RefreshToken> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        Jackson2JsonRedisSerializer<RefreshToken> serializer = new Jackson2JsonRedisSerializer<>(RefreshToken.class);
        template.setValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());

        return template;
    }
}
