package com.neuma573.autoboard.user.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.neuma573.autoboard.user.model.enums.BanReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlackList {

    @Getter
    private String ipAddress;

    @Getter
    private BanReason reason;

    @Getter
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expiryDate;

    public static BlackList generateBlackList(String ip, BanReason reason) {
        return BlackList.builder()
                .ipAddress(ip)
                .reason(reason)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
    }
}