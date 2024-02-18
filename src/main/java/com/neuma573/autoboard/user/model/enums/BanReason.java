package com.neuma573.autoboard.user.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BanReason {
    RATE_LIMIT_EXCEEDED("Rate limit exceeded"),
    SUSPICIOUS_ACTIVITY("Suspicious activity detected"),
    VIOLATION_OF_TERMS("Violation of terms of service"),
    SPAMMING("Spamming activities detected");

    private final String reason;
}
