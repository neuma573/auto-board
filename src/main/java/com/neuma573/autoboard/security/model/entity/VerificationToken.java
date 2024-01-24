package com.neuma573.autoboard.security.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.neuma573.autoboard.user.model.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {

    @Getter
    private String token;

    @Getter
    private String email;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Getter
    private LocalDateTime expiryDate;

    public static VerificationToken generateVerificationToken(User user) {
        return VerificationToken
                .builder()
                .token(UUID.randomUUID().toString())
                .email(user.getEmail())
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
    }
}
