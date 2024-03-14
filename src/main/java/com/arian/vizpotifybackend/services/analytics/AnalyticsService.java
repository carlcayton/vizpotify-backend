package com.arian.vizpotifybackend.services.analytics;

import com.arian.vizpotifybackend.dto.analytics.AnalyticsDTO;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.properties.AWSLambdaProperties;
import com.arian.vizpotifybackend.services.redis.AnalyticsCacheService;
import com.arian.vizpotifybackend.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
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

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserService userService;
    private final AnalyticsCacheService analyticsCacheService;
    private final RestTemplate restTemplate;
    private final AWSLambdaProperties awsLambdaProperties;

    public AnalyticsDTO getAnalyticsForUser(String userId) {
        Optional<UserDetail> userDetailOpt = userService.findBySpotifyId(userId);
        String processingKey = analyticsCacheService.getProcessingKey(userId);
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
    }
    public void updateAnalyticsAvailable(String spotifyId, boolean analyticsAvailable) {
        userService.findBySpotifyId(spotifyId).ifPresent(user -> {
            user.setAnalyticsAvailable(analyticsAvailable);
            userService.save(user);
        });
    }

    private AnalyticsDTO fetchAnalyticsData(String userId, boolean analyticsAvailable) {
        String url = awsLambdaProperties.endpoint() +userId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
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
