package com.arian.vizpotifybackend.services.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalyticsCacheService {

        private final RedisTemplate<String, String> myStringRedisTemplate;

        public String getProcessingKey(String userId) {
            return "analytics_processing_" + userId;
        }

        public boolean isProcessing(String processingKey) {
            return Optional.ofNullable(myStringRedisTemplate.opsForValue().get(processingKey)).isPresent();
        }

        public void setProcessing(String processingKey) {
            myStringRedisTemplate.opsForValue().set(processingKey, "true");
        }

        public void clearProcessing(String processingKey) {
            myStringRedisTemplate.delete(processingKey);
        }
}
