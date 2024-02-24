package com.neuma573.autoboard.global.interceptor;

import com.neuma573.autoboard.global.config.RateLimiter;
import com.neuma573.autoboard.global.exception.RateLimitExceededException;
import com.neuma573.autoboard.global.exception.UserBlockedException;
import com.neuma573.autoboard.global.utils.RequestUtils;
import com.neuma573.autoboard.security.utils.JwtProvider;
import com.neuma573.autoboard.user.model.entity.BlackList;
import com.neuma573.autoboard.user.service.UserService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.neuma573.autoboard.user.model.enums.BanReason.SUSPICIOUS_ACTIVITY;

@RequiredArgsConstructor
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final RateLimiter rateLimiter;

    private final JwtProvider jwtProvider;

    private final RedisTemplate<String, BlackList> blackListRedisTemplate;

    private final RedisTemplate<String, String> redisTemplate;

    private final UserService userService;

    private final Set<String> methodsToCheck = Set.of("POST", "PUT", "DELETE");

    private final static long BAN_TIME = 24 * 60 * 60; // 24시간

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) {
        String apiKey = RequestUtils.getClientIpAddress(httpServletRequest);

        if (shouldApplyRateLimit(httpServletRequest) && isBlacklisted(apiKey)) {
            throw new UserBlockedException(apiKey);
        }

        if (exceedsAbnormalRequestRate(apiKey)) {
            blackListRedisTemplate.opsForValue().set(apiKey , BlackList.generateBlackList(apiKey, SUSPICIOUS_ACTIVITY), BAN_TIME, TimeUnit.SECONDS);
            userService.setBan(jwtProvider.parseUserIdSafely(httpServletRequest));
            throw new UserBlockedException(apiKey);
        }

        if (shouldApplyRateLimit(httpServletRequest)) {
            String action = determineAction(RequestUtils.getRequestUri(httpServletRequest));
            applyRateLimit(apiKey, action);
        }
        return true;
    }

    private boolean isBlacklisted(String apiKey) {
        return blackListRedisTemplate.opsForValue().get(apiKey) != null;
    }

    private boolean exceedsAbnormalRequestRate(String apiKey) {
        trackRequest(apiKey);
        return isAbnormalRequest(apiKey);
    }


    private boolean shouldApplyRateLimit(HttpServletRequest httpServletRequest) {
        return methodsToCheck.contains(httpServletRequest.getMethod().toUpperCase());
    }

    private String determineAction(String path) {
        if (path.contains("/post")) {
            return "post";
        } else if (path.contains("/comment")) {
            return "comment";
        } else if (path.contains("/user")) {
            return "user";
        } else if (path.contains("/auth")) {
            return "auth";
        }
        return "other";
    }

    private void applyRateLimit(String apiKey, String action) throws RateLimitExceededException {
        Bucket bucket = rateLimiter.getBucket(action, apiKey);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            throw new RateLimitExceededException();
        }
    }

    private void trackRequest(String apiKey) {
        String key = "requestCount:" + apiKey;
        Long requestCount = redisTemplate.opsForValue().increment(key);

        if (requestCount != null && requestCount == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(1));
        }
    }

    private boolean isAbnormalRequest(String apiKey) {
        String key = "requestCount:" + apiKey;
        String value = redisTemplate.opsForValue().get(key);
        return value != null && Integer.parseInt(value) > 3;
    }
}