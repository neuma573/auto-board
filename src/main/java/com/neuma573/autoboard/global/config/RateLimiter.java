package com.neuma573.autoboard.global.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    public Bucket getBucket(String action, String key) {
        String bucketKey = action + "_" + key; // 행위와 API 키를 조합한 문자열을 고유 키로 사용
        return buckets.computeIfAbsent(bucketKey, k -> createNewBucket(bucketKey));
    }

    private Bucket createNewBucket(String bucketKey) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(3)
                .refillGreedy(1, Duration.ofSeconds(10))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}
