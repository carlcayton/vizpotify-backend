package com.arian.vizpotifybackend.integration.services;

import com.arian.vizpotifybackend.config.SecurityConfig;
import com.arian.vizpotifybackend.dto.analytics.*;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.properties.AWSLambdaProperties;
import com.arian.vizpotifybackend.repository.analytics.UserArtistTrackCountRepository;
import com.arian.vizpotifybackend.repository.analytics.UserGenreDistributionRepository;
import com.arian.vizpotifybackend.repository.analytics.UserMusicEraSummaryRepository;
import com.arian.vizpotifybackend.repository.analytics.UserTrackFeatureStatsRepository;
import com.arian.vizpotifybackend.services.analytics.AnalyticsService;
import com.arian.vizpotifybackend.services.redis.AnalyticsCacheService;
import com.arian.vizpotifybackend.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(properties = {"FRONTEND_URL=http://mocked-frontend-url"})
@ActiveProfiles("test")

class AnalyticsServiceIntegrationTest {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private RestTemplate restTemplate;

    @MockBean
    private UserService userService;

    @MockBean
    private AnalyticsCacheService analyticsCacheService;

    @MockBean
    private AWSLambdaProperties awsLambdaProperties;

    @MockBean
    private UserGenreDistributionRepository userGenreDistributionRepository;

    @MockBean
    private UserTrackFeatureStatsRepository userTrackFeatureStatsRepository;

    @MockBean
    private UserMusicEraSummaryRepository userMusicEraSummaryRepository;

    @MockBean
    private UserArtistTrackCountRepository userArtistTrackCountRepository;


    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getAnalyticsForUser_shouldReturnCachedAnalytics_whenPresent() {
        // Arrange
        String userId = "user123";
        Map<String, List<AudioFeatureDTO>> audioFeatureDTOMap = new HashMap<>();
        Map<String, List<GenreDistributionDTO>> genreDistributionDTOMap = new HashMap<>();
        Map<String, List<MusicEraSummaryDTO>> musicEraSummaryDTOMap = new HashMap<>();
        Map<String, List<ArtistTrackCountDTO>> artistTrackCountDTOMap = new HashMap<>();
        AnalyticsDTO cachedAnalytics = new AnalyticsDTO(audioFeatureDTOMap, genreDistributionDTOMap, musicEraSummaryDTOMap, artistTrackCountDTOMap);
        when(analyticsCacheService.getUserAnalyticsFromCache(userId)).thenReturn(Optional.of(cachedAnalytics));

        // Act
        AnalyticsDTO result = analyticsService.getAnalyticsForUser(userId);

        // Assert
        assertEquals(cachedAnalytics, result);
        verify(analyticsCacheService, times(1)).getUserAnalyticsFromCache(userId);
    }



    @Test
    void getAnalyticsForUser_shouldRetrieveFreshAnalyticsAndCache_whenCachedAnalyticsNotPresent() {
        // Arrange
        String userId = "user123";
        when(analyticsCacheService.getUserAnalyticsFromCache(userId)).thenReturn(Optional.empty());
        when(analyticsCacheService.isAnalyticsProcessing(userId)).thenReturn(false);
        when(userService.findBySpotifyId(userId)).thenReturn(Optional.of(new UserDetail()));
        when(awsLambdaProperties.analyticsEndpoint()).thenReturn("http://analytics-service/analytics/");

        String responseBody = "{ \"audio_features\": {}, \"genre_distribution\": {}, \"music_era_summary\": {}, \"artist_track_count\": {} }";
        mockServer.expect(requestTo("http://analytics-service/analytics/" + userId))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.analytics_available").value(false))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        // Act
        AnalyticsDTO result = analyticsService.getAnalyticsForUser(userId);

        // Assert
        assertNotNull(result);
        verify(analyticsCacheService, times(1)).cacheUserAnalytics(eq(userId), any(AnalyticsDTO.class));
        verify(userService, times(1)).setAnalyticsAvailable(userId, true);
    }

}