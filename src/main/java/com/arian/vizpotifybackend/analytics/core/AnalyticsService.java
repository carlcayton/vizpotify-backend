package com.arian.vizpotifybackend.analytics.core;


import com.arian.vizpotifybackend.cache.AnalyticsCacheService;
import com.arian.vizpotifybackend.user.core.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsCacheService analyticsCacheService;
    private final AnalyticsAggregator analyticsAggregator;
    private final UserService userService;

    public AnalyticsDto getAnalyticsForUser(String userId) {
        log.info("Getting analytics for user: {}", userId);

        Optional<AnalyticsDto> cachedAnalytics = analyticsCacheService.getUserAnalyticsFromCache(userId);

        if (cachedAnalytics.isPresent()) {
            log.info("Returning cached analytics for user: {}", userId);
            return cachedAnalytics.get();
        }

        AnalyticsDto output = retrieveFreshAnalyticsAndCache(userId);
        log.info("Completed getting analytics for user: {}", userId);
        return output;
    }

    private AnalyticsDto retrieveFreshAnalyticsAndCache(String userId) {
        log.info("Retrieving fresh analytics for user: {}", userId);
        boolean isUserAnalyticsAvailable = userService.isAnalyticsAvailable(userId);
        System.out.println(isUserAnalyticsAvailable);
        AnalyticsDto analyticsDto = analyticsAggregator.getAllAnalyticsForUser(userId, isUserAnalyticsAvailable);

        analyticsCacheService.cacheUserAnalytics(userId, analyticsDto);
        log.info("Caching analytics data", keyValue("userId", userId), keyValue("analyticsData", analyticsDto));

        userService.setAnalyticsAvailable(userId, true);
        log.info("Successfully retrieved and cached analytics for user: {}", userId);
        return analyticsDto;
    }
}

