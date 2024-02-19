package com.arian.vizpotifybackend.services.user;

import java.util.concurrent.CompletableFuture;

import com.arian.vizpotifybackend.dto.AnalyticsDTO;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.repository.UserDetailRepository;
import com.arian.vizpotifybackend.services.redis.AnalyticsCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final RestTemplate restTemplate;
    private final UserService userService;
    private final AnalyticsCacheService analyticsCacheService;

    @Value("${lambda.base-url}")
    private String lambdaBaseUrl;

    @Async
    public CompletableFuture<AnalyticsDTO> getAnalyticsForUser(String userId) {
        Optional<UserDetail> userDetailOpt = userService.findBySpotifyId(userId);
        String processingKey = analyticsCacheService.getProcessingKey(userId);
        return CompletableFuture.supplyAsync(() -> {
            boolean isProcessing = analyticsCacheService.isProcessing(processingKey);
            boolean analyticsAvailable = userDetailOpt.map(UserDetail::isAnalyticsAvailable).orElse(false);

            if (analyticsAvailable || isProcessing) {
                return null;
            }

            analyticsCacheService.setProcessing(processingKey);
            try {
                AnalyticsDTO analyticsData = fetchAnalyticsData(userId, false);
                userDetailOpt.ifPresent(userDetail -> userService.setAnalyticsAvailable(userId, true));
                return analyticsData;
            } finally {
                analyticsCacheService.clearProcessing(processingKey);
            }
        });
    }
    private AnalyticsDTO fetchAnalyticsData(String userId, boolean analyticsAvailable) {
        String url = lambdaBaseUrl+ "/consolidated-analytics";
        HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_id", userId);
        requestBody.put("analytics_available", analyticsAvailable);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        try {
            return new ObjectMapper().readValue(response.getBody(), AnalyticsDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }
}
