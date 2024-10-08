package com.arian.vizpotifybackend.analytics.features;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/{userId}/trackFeatureStats")
@RequiredArgsConstructor
public class UserTrackFeatureStatsController {

    private final UserTrackFeatureStatsService userTrackFeatureStatsService;

    @GetMapping
    public ResponseEntity<UserTrackFeatureStatsMapDto> getUserTrackFeatureStats(@PathVariable String userId) {
        Map<String, UserTrackFeatureStatsDto> featureStats = userTrackFeatureStatsService.fetchUserTrackFeatureStats(userId);
        UserTrackFeatureStatsMapDto response = new UserTrackFeatureStatsMapDto(userId, featureStats);
        return ResponseEntity.ok(response);
    }
}