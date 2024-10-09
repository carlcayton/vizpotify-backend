package com.arian.vizpotifybackend.analytics;

import com.arian.vizpotifybackend.analytics.era.UserMusicEraSummaryDto;
import com.arian.vizpotifybackend.analytics.era.UserMusicEraSummaryService;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsMapDto;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsService;
import com.arian.vizpotifybackend.analytics.genre.UserGenreDistributionService;
import com.arian.vizpotifybackend.analytics.genre.UserGenreDistributionMapDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final UserMusicEraSummaryService userMusicEraSummaryService;
    private final UserTrackFeatureStatsService userTrackFeatureStatsService;
    private final UserGenreDistributionService userGenreDistributionService;

//    @GetMapping("/users/{userId}/musicEraSummary")
//    public ResponseEntity<Map<String, Map<String, UserMusicEraSummaryDto>>> getUserMusicEraSummary(@PathVariable String userId) {
//        Map<String, Map<String, UserMusicEraSummaryDto>> summary = userMusicEraSummaryService.fetchUserMusicEraSummary(userId);
//        return ResponseEntity.ok(summary);
//    }

    @GetMapping("/users/{userId}/trackFeatureStats")
    public ResponseEntity<UserTrackFeatureStatsMapDto> getUserTrackFeatureStats(@PathVariable String userId) {
        UserTrackFeatureStatsMapDto featureStats = userTrackFeatureStatsService.fetchUserTrackFeatureStats(userId);
        return ResponseEntity.ok(featureStats);
    }

    @GetMapping("/users/{userId}/genreDistribution")
    public ResponseEntity<UserGenreDistributionMapDto> getUserGenreDistribution(@PathVariable String userId) {
        UserGenreDistributionMapDto genreDistribution = userGenreDistributionService.fetchUserGenreDistribution(userId);
        return ResponseEntity.ok(genreDistribution);
    }
}
