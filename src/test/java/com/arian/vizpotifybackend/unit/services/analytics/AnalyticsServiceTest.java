package com.arian.vizpotifybackend.unit.services.analytics;

import com.arian.vizpotifybackend.analytics.core.AnalyticsAggregator;
import com.arian.vizpotifybackend.analytics.core.AnalyticsDto;
import com.arian.vizpotifybackend.analytics.core.AnalyticsService;
import com.arian.vizpotifybackend.cache.AnalyticsCacheService;
import com.arian.vizpotifybackend.user.core.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private AnalyticsCacheService analyticsCacheService;

    @Mock
    private AnalyticsAggregator analyticsAggregator;

    @Mock
    private UserService userService;

    @InjectMocks
    private AnalyticsService analyticsService;

    private String userId;
    private AnalyticsDto analyticsDto;

    @BeforeEach
    void setUp() {
        userId = "user123";
    }

    @Test
    void getAnalyticsForUser_shouldReturnCachedAnalytics_whenCacheExists() {
        when(analyticsCacheService.getUserAnalyticsFromCache(userId)).thenReturn(Optional.of(analyticsDto));

        AnalyticsDto result = analyticsService.getAnalyticsForUser(userId);

        assertEquals(analyticsDto, result);
        verify(analyticsCacheService, times(1)).getUserAnalyticsFromCache(userId);
        verifyNoInteractions(analyticsAggregator, userService);
    }

    @Test
    void getAnalyticsForUser_shouldRetrieveFreshAnalytics_whenCacheDoesNotExist() {
        when(analyticsCacheService.getUserAnalyticsFromCache(userId)).thenReturn(Optional.empty());
        when(userService.isAnalyticsAvailable(userId)).thenReturn(true);
        when(analyticsAggregator.getAllAnalyticsForUser(userId, true)).thenReturn(analyticsDto);

        AnalyticsDto result = analyticsService.getAnalyticsForUser(userId);

        assertEquals(analyticsDto, result);
        verify(analyticsCacheService, times(1)).getUserAnalyticsFromCache(userId);
        verify(userService, times(1)).isAnalyticsAvailable(userId);
        verify(analyticsAggregator, times(1)).getAllAnalyticsForUser(userId, true);
        verify(analyticsCacheService, times(1)).cacheUserAnalytics(userId, analyticsDto);
        verify(userService, times(1)).setAnalyticsAvailable(userId, true);
    }
}

