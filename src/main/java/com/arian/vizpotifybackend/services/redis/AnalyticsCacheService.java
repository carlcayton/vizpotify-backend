package com.arian.vizpotifybackend.services.redis;

import com.arian.vizpotifybackend.dto.analytics.AnalyticsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AnalyticsCacheService {

    private final RedisTemplate<String, String> myStringRedisTemplate;
    private final RedisTemplate<String, Object> jsonRedisTemplate;

    private static final String USER_ANALYTICS_PROCESSING_KEY_PREFIX = "user:analytics:processing:";
    private static final String USER_ANALYTICS_KEY_PREFIX = "user:analytics:";

    public void markAnalyticsProcessing(String userId) {
        String key = USER_ANALYTICS_PROCESSING_KEY_PREFIX + userId;
        jsonRedisTemplate.opsForValue().set(key, Boolean.TRUE, 10, TimeUnit.MINUTES); // Mark as processing, adjust time as needed
    }

    public boolean isAnalyticsProcessing(String userId) {
        String key = USER_ANALYTICS_PROCESSING_KEY_PREFIX + userId;
        Boolean processing = (Boolean) jsonRedisTemplate.opsForValue().get(key);
        return Boolean.TRUE.equals(processing);
    }

    public void clearAnalyticsProcessing(String userId) {
        String key = USER_ANALYTICS_PROCESSING_KEY_PREFIX + userId;
        jsonRedisTemplate.delete(key);
    }

    public void cacheUserAnalytics(String userId, AnalyticsDTO analyticsDTO) {
        String key = USER_ANALYTICS_KEY_PREFIX + userId;
        jsonRedisTemplate.opsForValue().set(key, analyticsDTO, 1, TimeUnit.DAYS); // Cache for 1 day, adjust as needed
    }

    public Optional<AnalyticsDTO> getUserAnalyticsFromCache(String userId) {
        String key = USER_ANALYTICS_KEY_PREFIX + userId;
        AnalyticsDTO analyticsDTO = (AnalyticsDTO) jsonRedisTemplate.opsForValue().get(key);
        return Optional.ofNullable(analyticsDTO);
    }

}