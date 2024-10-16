package com.arian.vizpotifybackend.analytics;

import com.arian.vizpotifybackend.analytics.artist.UserArtistTrackCountMapDto;
import com.arian.vizpotifybackend.analytics.artist.UserArtistTrackCountService;
import com.arian.vizpotifybackend.analytics.era.UserMusicEraSummaryMapDto;
import com.arian.vizpotifybackend.analytics.era.UserMusicEraSummaryService;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsMapDto;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsService;
import com.arian.vizpotifybackend.analytics.genre.UserGenreDistributionService;
import com.arian.vizpotifybackend.analytics.genre.UserGenreDistributionMapDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final UserMusicEraSummaryService userMusicEraSummaryService;
    private final UserTrackFeatureStatsService userTrackFeatureStatsService;
    private final UserGenreDistributionService userGenreDistributionService;
    private final UserArtistTrackCountService userArtistTrackCountService;

    @GetMapping("/users/{userId}/musicEraSummary")
    public ResponseEntity<?> getUserMusicEraSummary(@PathVariable String userId) {
        AnalyticsResponse<UserMusicEraSummaryMapDto> response = userMusicEraSummaryService.fetchUserMusicEraSummary(userId);
        if ("processing".equals(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }
        return ResponseEntity.ok(response.getData());
    }

    @GetMapping("/users/{userId}/trackFeatureStats")
    public ResponseEntity<?> getUserTrackFeatureStats(@PathVariable String userId) {
        AnalyticsResponse<UserTrackFeatureStatsMapDto> response = userTrackFeatureStatsService.fetchUserTrackFeatureStats(userId);
        if ("processing".equals(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }
        return ResponseEntity.ok(response.getData());
    }

    @GetMapping("/users/{userId}/genreDistribution")
    public ResponseEntity<?> getUserGenreDistribution(@PathVariable String userId) {
        AnalyticsResponse<UserGenreDistributionMapDto> response = userGenreDistributionService.fetchUserGenreDistribution(userId);
        if ("processing".equals(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }
        return ResponseEntity.ok(response.getData());
    }

    @GetMapping("/users/{userId}/artistTrackCount")
    public ResponseEntity<?> getUserArtistTrackCount(@PathVariable String userId) {
        AnalyticsResponse<UserArtistTrackCountMapDto> response = userArtistTrackCountService.fetchUserArtistTrackCount(userId);
        if ("processing".equals(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }
        return ResponseEntity.ok(response.getData());
    }
}

