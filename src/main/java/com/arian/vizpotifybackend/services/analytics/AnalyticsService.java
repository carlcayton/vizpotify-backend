package com.arian.vizpotifybackend.services.analytics;

import com.arian.vizpotifybackend.dto.analytics.AnalyticsDTO;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.properties.AWSLambdaProperties;
import com.arian.vizpotifybackend.repository.analytics.UserArtistTrackCountRepository;
import com.arian.vizpotifybackend.repository.analytics.UserGenreDistributionRepository;
import com.arian.vizpotifybackend.repository.analytics.UserMusicEraSummaryRepository;
import com.arian.vizpotifybackend.repository.analytics.UserTrackFeatureStatsRepository;
import com.arian.vizpotifybackend.services.redis.AnalyticsCacheService;
import com.arian.vizpotifybackend.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserService userService;
    private final AnalyticsCacheService analyticsCacheService;
    private final RestTemplate restTemplate;
    private final AWSLambdaProperties awsLambdaProperties;
    private final UserGenreDistributionRepository userGenreDistributionRepository;
    private final UserTrackFeatureStatsRepository userTrackFeatureStatsRepository;
    private final UserMusicEraSummaryRepository userMusicEraSummaryRepository;
    private final UserArtistTrackCountRepository userArtistTrackCountRepository;

    public AnalyticsDTO getAnalyticsForUser(String userId) {
        log.info("Getting analytics for user: {}", userId);
        Optional<UserDetail> userDetailOpt = userService.findBySpotifyId(userId);
        boolean analyticsAvailable = userDetailOpt.map(UserDetail::isAnalyticsAvailable).orElse(false);

        Optional<AnalyticsDTO> cachedAnalytics = analyticsCacheService.getUserAnalyticsFromCache(userId);

        if (analyticsCacheService.isAnalyticsProcessing(userId)) {
            log.info("Analytics processing is currently in progress for user: {}", userId);
            return null;
        }

        if (cachedAnalytics.isPresent()) {
            log.info("Returning cached analytics for user: {}", userId);
            return cachedAnalytics.get();
        }
        AnalyticsDTO output;
        log.debug("User exists check for user {}: {}", userId, userExists(userId));

        if (userExists(userId)) {
            output = updateAndCacheAnalytics(userId);
        }else{
             output = retrieveFreshAnalyticsAndCache(userId, userDetailOpt);
        }
        log.info("Completed getting analytics for user: {}", userId);
        return output;
    }

    public boolean userExists(String userId) {
        return userGenreDistributionRepository.existsByUserSpotifyId(userId) && userTrackFeatureStatsRepository.existsByUserSpotifyId(userId) && userMusicEraSummaryRepository.existsByUserSpotifyId(userId) && userArtistTrackCountRepository.existsByUserSpotifyId(userId);
    }

    private AnalyticsDTO retrieveFreshAnalyticsAndCache(String userId, Optional<UserDetail> userDetailOpt) {
        analyticsCacheService.markAnalyticsProcessing(userId);
        log.info("Retrieving fresh analytics for user: {}", userId);
        try {
            AnalyticsDTO analyticsData = requestAnalyticsFromExternalService(userId, false);

            analyticsCacheService.cacheUserAnalytics(userId, analyticsData);
            userDetailOpt.ifPresent(userDetail -> userService.setAnalyticsAvailable(userId, true));
            log.info("Caching analytics data", keyValue("userId", userId), keyValue("analyticsData", analyticsData));

            log.info("Successfully retrieved and cached analytics for user: {}", userId);
            return analyticsData;
        } finally {
            analyticsCacheService.clearAnalyticsProcessing(userId);
        }
    }

    private AnalyticsDTO updateAndCacheAnalytics(String userId) {
        analyticsCacheService.markAnalyticsProcessing(userId);
        try {
            AnalyticsDTO analyticsData = requestAnalyticsFromExternalService (userId, true);
            analyticsCacheService.cacheUserAnalytics(userId, analyticsData);
            return analyticsData;
        } finally {
            analyticsCacheService.clearAnalyticsProcessing(userId);
        }
    }

    public AnalyticsDTO requestAnalyticsFromExternalService(String userId, boolean analyticsAvailable) {
        String url = awsLambdaProperties.analyticsEndpoint() + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("analytics_available", analyticsAvailable);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            AnalyticsDTO analyticsDTO = new ObjectMapper().readValue(response.getBody(), AnalyticsDTO.class);
            if (analyticsDTO.equals(null)){
                throw new RuntimeException("Error calling external service");
            }
            return analyticsDTO;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("API call error: Status code {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error calling external service", e);
        } catch (Exception e) {
            log.error("Error processing request to external service", e);
            throw new RuntimeException("Unexpected error", e);
        }
    }

}